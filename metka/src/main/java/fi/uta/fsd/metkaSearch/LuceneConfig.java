/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metkaSearch;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.util.Version;

public final class LuceneConfig {
    // This class is not meant to be extended
    private LuceneConfig() {}

    /**
     * FieldType used with LongFields. Field is not stored
     */
    public static final FieldType LONG_TYPE;

    /**
     * FieldType used with DoubleFields. Field is not stored
     */
    public static final FieldType DOUBLE_TYPE;

    /**
     * FieldType used with LongFields. Field is stored
     */
    public static final FieldType LONG_TYPE_STORE;

    /**
     * FieldType used with DoubleFields. Field is stored
     */
    public static final FieldType DOUBLE_TYPE_STORE;

    /**
     * Numeric precision step used throughout the system
     */
    public static final Integer PRECISION_STEP = 1;

    /**
     * Lucene version used throughout the software
     */
    public static final Version USED_VERSION = Version.LUCENE_48;
    /**
     * Defines how long (in ms) indexing has to be idle before index is flushed to disk.
     */
    public static final int TIME_IDLING_BEFORE_FLUSH = 1000;
    /**
     * Defines if indexing will take a break after multiple commands to flush changes to disk.
     */
    public static boolean FORCE_FLUSH_AFTER_BATCH_OF_CHANGES = true;
    /**
     * Defines maximum number of changes to index between flushes if FORCE_FLUSH_AFTER_BATCH_OF_CHANGES is true.
     */
    public static final int MAX_CHANGE_BATCH_SIZE = 500;

    public static final int MAX_RETURNED_RESULTS = 20000;

    public static final EnglishAnalyzer ENGLISH_ANALYZER;
    public static final SwedishAnalyzer SWEDISH_ANALYZER;

    static {
        // Set LongType no store
        LongField tempLong;
        DoubleField tempDouble;
        FieldType tempType;

        tempLong = new LongField("temp", 1L, Field.Store.NO);
        tempType = new FieldType(tempLong.fieldType());
        tempType.setNumericPrecisionStep(PRECISION_STEP);
        tempType.freeze();
        LONG_TYPE = tempType;

        tempLong = new LongField("temp", 1L, Field.Store.YES);
        tempType = new FieldType(tempLong.fieldType());
        tempType.setNumericPrecisionStep(PRECISION_STEP);
        tempType.freeze();
        LONG_TYPE_STORE = tempType;

        tempDouble = new DoubleField("temp", 1L, Field.Store.NO);
        tempType = new FieldType(tempDouble.fieldType());
        tempType.setNumericPrecisionStep(PRECISION_STEP);
        tempType.freeze();
        DOUBLE_TYPE = tempType;

        tempDouble = new DoubleField("temp", 1L, Field.Store.YES);
        tempType = new FieldType(tempDouble.fieldType());
        tempType.setNumericPrecisionStep(PRECISION_STEP);
        tempType.freeze();
        DOUBLE_TYPE_STORE = tempType;

        ENGLISH_ANALYZER  = new EnglishAnalyzer(USED_VERSION);
        SWEDISH_ANALYZER = new SwedishAnalyzer(USED_VERSION);

    }
}
