package com.nikondsl.cryptoFolder.scanner;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

public class FolderScanner {
	private String folderNameToEncrypt="/home/tanik/Завантаження/Encripted";
	private String folderNameToObserve="/home/tanik/Завантаження/Line";
	private ConcurrentMap<String, Callable> jobs = new ConcurrentHashMap<>();
	
	public static void main(String[] args) throws IOException {
		
		FolderScanner folderScanner = new FolderScanner();
		while (true) {
			folderScanner.lookUp();
			try {
				Thread.currentThread().sleep(10_000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			System.err.println("next....");
		}
	}
	
	private void lookUp() throws IOException {
		Path observePath = Paths.get(folderNameToObserve);
		if (!observePath.toFile().exists() ||
			!observePath.toFile().canRead()) {
			System.err.println("Cannot read " + folderNameToObserve);
			return;
		}
		ExecutorService service = Executors.newFixedThreadPool(5);
		
		for (Path path : scan()) {
			jobs.putIfAbsent(path.toFile().getAbsolutePath(),
					createWorker(service, path));
		}
		
		service.shutdown();
		try {
			service.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Interrupted.");
		}
		System.err.println("finished!!!!");
	}
	
	private Callable createWorker(ExecutorService service, Path path) {
		Callable callable = () -> {
	
			String newFilePath = path.toFile().getAbsolutePath().replace(folderNameToObserve, folderNameToEncrypt);
			Paths.get(newFilePath).toFile().getParentFile().mkdirs();
			IOUtils.copyLarge(new FileInputStream(path.toFile()),
					new FileOutputStream(newFilePath + "_encripted"));
			System.out.println("processing... " + path.toFile().getAbsolutePath() + "->" + newFilePath + "_encripted");
			return null;
		};
		service.submit(callable);
		return callable;
	}
	
	private List<Path> scan() throws IOException {
		Path observePath = Paths.get(folderNameToObserve);
		if (!observePath.toFile().exists() || !observePath.toFile().canRead()) {
			System.err.println("Cannot read " + folderNameToObserve);
			return Collections.emptyList();
		}
		List<Path> result = new ArrayList<>();
		Files.walkFileTree(observePath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
				result.add(path);
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
				System.err.println(e);
				return FileVisitResult.CONTINUE;
			}
		});
		
		return result;
	}
}
