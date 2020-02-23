package com.abnamro.dailysummaryreport.header;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

import java.io.IOException;
import java.io.Writer;

public class DailySummaryReportHeaderCallBack implements FlatFileHeaderCallback {

    private final String header;

    public DailySummaryReportHeaderCallBack(String header) {
        this.header = header;
    }
    @Override
    public void writeHeader(Writer writer) throws IOException {
        writer.write(header);
    }
}
