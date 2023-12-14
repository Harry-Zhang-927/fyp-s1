package com.example.demo.parallel;

import com.example.demo.client.utils.MD5Util;
import com.example.demo.client.utils.SecurityUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.RecursiveTask;

public class CsvTask extends RecursiveTask<List<String>> {
    private final List<String> lines;
    private final int start;
    private final int end;
    private final int rowsPerBlock;

    public CsvTask(List<String> lines, int start, int end, int rowsPerBlock) {
        this.lines = lines;
        this.start = start;
        this.end = end;
        this.rowsPerBlock = rowsPerBlock;
    }

    @Override
    protected List<String> compute() {
        if (end - start <= rowsPerBlock) {
            // 对子列表创建一个深拷贝，而不是使用 subList
            List<String> blockCopy = new ArrayList<>(lines.subList(start, end));
            try {
                return processBlock(blockCopy);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            int mid = start + (end - start) / 2;
            CsvTask task1 = new CsvTask(lines, start, mid, rowsPerBlock);
            CsvTask task2 = new CsvTask(lines, mid, end, rowsPerBlock);

            task1.fork();
            List<String> task2Result = task2.compute();
            List<String> task1Result = task1.join();

            List<String> mergedResult = new ArrayList<>(task1Result);
            mergedResult.addAll(task2Result);
            return mergedResult;
        }
    }


    private List<String> processBlock(List<String> blockLines) throws GeneralSecurityException, IOException {
        // 这里实现具体的数据块处理逻辑
        // 计算MD5、随机插入签名等
        List<String> blockCopy = new ArrayList<>(blockLines);
        if (blockCopy.isEmpty()) {
            return new ArrayList<>();
        }

        String blockAsString = String.join("\n", blockCopy);
        String blockMd5 = MD5Util.calculateMD5(blockAsString);

        // 假设这里的 SecurityUtils.sign 方法存在且可用
        byte[] signature = SecurityUtils.sign(SecurityUtils.getKeyPair(SecurityUtils.CLIENT_PUBLIC_KEY_FILE, SecurityUtils.CLIENT_PRIVATE_KEY_FILE).getPrivate(), blockMd5.getBytes());

        Random rand = new Random();
        int n = rand.nextInt(blockCopy.size()); // 不再需要 +1，因为这里不处理 header

        // 在随机位置插入MD5签名
        blockCopy.add(n, "Signature:" + Arrays.toString(signature));

        return blockCopy;
    }
}

