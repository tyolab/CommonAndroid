package au.com.tyo.android.services;

public interface ResourceFetchererInterface<FileType, ContainerType> {

	void handleResult(ContainerType container, FileType file);

}
