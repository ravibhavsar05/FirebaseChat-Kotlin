package droidninja.filepicker.cursors.loadercallbacks;

import java.util.List;
import java.util.Map;

import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;

public interface FileMapResultCallback {
    void onResultCallback(Map<FileType, List<Document>> files);
}

