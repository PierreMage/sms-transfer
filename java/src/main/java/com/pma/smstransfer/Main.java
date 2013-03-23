package com.pma.smstransfer;

import java.io.IOException;

public class Main {

    public static void main(final String[] args) throws ClassNotFoundException, IOException {
        Ios6MessagesToCsvConverter ios6MessagesToCsvConverter = new Ios6MessagesToCsvConverter(args);
        ios6MessagesToCsvConverter.writeCsv();
    }
}
