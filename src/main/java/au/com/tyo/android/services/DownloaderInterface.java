package au.com.tyo.android.services;

public interface DownloaderInterface<FileType, ContainerType> {
	void handleResult(ContainerType container, FileType file);
}
