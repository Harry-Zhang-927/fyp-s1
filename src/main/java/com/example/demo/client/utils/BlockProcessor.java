package com.example.demo.client.utils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockProcessor extends RecursiveTask<String> {
    private final List<String> block;
    private final String signatureToFind;
    private final AtomicBoolean found;

    public BlockProcessor(List<String> block, String signatureToFind, AtomicBoolean found) {
        this.block = block;
        this.signatureToFind = signatureToFind;
        this.found = found;
    }

    @Override
    protected String compute() {
        if (found.get()) {
            return null; // 如果已经找到，则直接返回
        }

        Optional<String> foundLine = block.stream()
                .filter(l -> l.contains(signatureToFind))
                .findFirst();

        if (foundLine.isPresent()) {
            block.remove(foundLine.get());
            String remainingBlock = String.join("\n", block);
            return MD5Util.calculateMD5(remainingBlock); // 假设这是您的MD5计算方法
        }
        return null;
    }
}
