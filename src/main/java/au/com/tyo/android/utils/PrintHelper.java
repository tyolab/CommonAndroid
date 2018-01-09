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

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 9/1/18.
 */

public class PrintHelper {

    public static final double MILLIMETER_TO_POINT = 2.83465;
    public static final double INCH_TO_POINT = 72;

    public static double millimeterToPoint(double v) {
        return v * MILLIMETER_TO_POINT;
    }

    public static int pointToMillis(int points) {
        return points / 72 * 1000;
    }
}
