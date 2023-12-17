package com.example.demo.parallel;

import com.example.demo.client.utils.MD5Util;
import com.example.demo.client.utils.SecurityUtils;
import com.example.demo.model.BlockProcessingVO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class CsvTask extends RecursiveTask<BlockProcessingVO> {
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
    protected BlockProcessingVO compute() {
        if (end - start <= rowsPerBlock) {
            // 对子列表创建一个深拷贝，而不是使用 subList
            List<String> blockCopy = new ArrayList<>(lines.subList(start, end));
            try {
                return processBlock(blockCopy);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 创建任务来并行处理每个数据块
            List<CsvTask> tasks = new ArrayList<>();
            for (int i = start; i < end; i += rowsPerBlock) {
                int nextEnd = Math.min(i + rowsPerBlock, end);
                tasks.add(new CsvTask(lines, i, nextEnd, rowsPerBlock));
            }

            // 并行执行所有任务
            invokeAll(tasks);

            // 合并结果
            List<String> mergedCsv = new ArrayList<>();
            List<String> mergedSignatures = new ArrayList<>();
            for (CsvTask task : tasks) {
                BlockProcessingVO result = task.join();
                mergedCsv.addAll(result.getCsv());
                mergedSignatures.addAll(result.getSignatures());
            }

            return new BlockProcessingVO(mergedSignatures, mergedCsv, null);
        }
    }


    private BlockProcessingVO processBlock(List<String> blockLines) throws GeneralSecurityException, IOException {
        // 这里实现具体的数据块处理逻辑
        // 计算MD5、随机插入签名等
        List<String> blockCopy = new ArrayList<>(blockLines);
        if (blockCopy.isEmpty()) {
            return new BlockProcessingVO();
        }

        String blockAsString = String.join("\n", blockCopy);
        String blockMd5 = MD5Util.calculateMD5(blockAsString);

        // 假设这里的 SecurityUtils.sign 方法存在且可用
        PrivateKey privateKey = SecurityUtils.getKeyPair(SecurityUtils.CLIENT_PUBLIC_KEY_FILE,
                SecurityUtils.CLIENT_PRIVATE_KEY_FILE).getPrivate();
        byte[] signature = SecurityUtils.sign(privateKey, blockMd5.getBytes());

        Random rand = new Random();
        int n = rand.nextInt(blockCopy.size()); // 不再需要 +1，因为这里不处理 header

        // 在随机位置插入MD5签名
        blockCopy.add(n, "Signature:" + Arrays.toString(signature));
        List<String> signatures = new LinkedList<>();
        signatures.add("Signature:" + Arrays.toString(signature));

        BlockProcessingVO blockProcessingVO = new BlockProcessingVO();
        blockProcessingVO.setCsv(blockCopy);
        blockProcessingVO.setSignatures(signatures);

        return blockProcessingVO;
    }
}

