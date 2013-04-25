package com.pma.smstransfer;

import java.io.IOException;

public class Main {

    public static void main(final String[] args) throws ClassNotFoundException, IOException {
        if (args.length != 4) {
            throw new IllegalArgumentException("Expecting 4 arguments: dbFilePath, outputFilePath, separator and newlineReplacement");
        }
        Ios6MessagesToCsvConverter ios6MessagesToCsvConverter = new Ios6MessagesToCsvConverter(args[0], args[1], args[2], args[3]);
        ios6MessagesToCsvConverter.writeCsv();
    }
}
