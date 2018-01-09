/*
 * Copyright (c) 2018 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package au.com.tyo.android.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 9/1/18.
 */

public class PdfUtils {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void writeBitmapToOutputStream(Bitmap bitmap, PdfDocument.PageInfo pageInfo, int left, int right, int density, OutputStream outputStream) throws IOException {
        //Create PDF document
        PdfDocument doc = new PdfDocument();
        // Create A4 sized PDF page
        // PageInfo pageInfo = new PageInfo.Builder(595,842,1).create();

        PdfDocument.Page page = doc.startPage(pageInfo);
        page.getCanvas().setDensity(density);

        page.getCanvas().drawBitmap(bitmap, left, right, null);

        doc.finishPage(page);
        doc.writeTo(outputStream);
        doc.close();
    }

    public static void writeBitmapToOutputStream(Bitmap bitmap, PdfDocument.PageInfo pageInfo, int left, int right, int density, File file) throws IOException {
        writeBitmapToOutputStream(bitmap, pageInfo, left, right, density, new FileOutputStream(file));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void writeBitmapToOutputStream(Bitmap bitmap, int width, int height, int left, int right, int density, File file) throws IOException {
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        writeBitmapToOutputStream(bitmap, pageInfo, left, right, density, new FileOutputStream(file));
    }
}
