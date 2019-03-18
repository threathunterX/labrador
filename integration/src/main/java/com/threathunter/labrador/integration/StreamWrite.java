package com.threathunter.labrador.integration;

import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.core.builder.idgenerator.FileVariableIdGenerator;
import com.threathunter.labrador.core.builder.idgenerator.VariableIdGenerator;
import com.threathunter.labrador.core.builder.update.EventFieldUpdate;
import com.threathunter.labrador.core.builder.update.VariableUpdate;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.StreamSendService;
import com.threathunter.labrador.core.transform.EnvExtract;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 */
public class StreamWrite {

    private String dataPath = "intergration_write.xlsx";

    private List<EventWrap> eventWraps;

    public StreamWrite() throws Exception {
        eventWraps = new ArrayList<>();
        init();
    }

    public void init() throws Exception {
        String idGenerator = ConfigUtil.getString("nebula.labrador.idgenerator.type");
        if (idGenerator.equals("file")) {
            String path = ConfigUtil.getString("nebula.labrador.idgenerator.path");
            VariableIdGenerator generator = new FileVariableIdGenerator(path);
            new EventFieldUpdate().update();
            VariableUpdate variableUpdate = new VariableUpdate(generator);
            variableUpdate.update();
        } else {
            throw new LabradorException("IdGenerator " + idGenerator + " not implement yet");
        }
    }

    public void process() throws IOException, InterruptedException, DataTypeNotMatchException, LabradorException {
        loadExcel();
        if (eventWraps.size() == 0) {
            throw new IllegalStateException("load excel fail, event size is 0");
        }

        for (EventWrap wrap : eventWraps) {
            StreamSendService sendService;
            if (StringUtils.isBlank(wrap.getVariable())) {
                sendService = new StreamSendService(new EnvExtract());
            } else {
                sendService = new StreamSendService(new SpecialVariableExtract(wrap.getVariable()));
            }
            sendService.process(wrap.getEvent());
        }

    }

    private void loadExcel() throws IOException {
        Workbook workbook;
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.dataPath);
        if (POIFSFileSystem.hasPOIFSHeader(input)) {
            workbook = new HSSFWorkbook(input);
        } else {
            workbook = new XSSFWorkbook(input);
        }
        Iterator<Sheet> iterator = workbook.iterator();
        RowMeta rowMeta = null;
        while (iterator.hasNext()) {
            Sheet sheet = iterator.next();
            for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
                Row row = sheet.getRow(j);
                if (j == 0) {
                    rowMeta = new RowMeta(row);
                } else {
                    EventWrap eventWrap = EventWrap.valueOf(row, rowMeta);
                    eventWraps.add(eventWrap);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        StreamWrite streamWrite = new StreamWrite();
        if (args.length > 0) {
            streamWrite.dataPath = args[0];
        }
        streamWrite.process();
        Thread.sleep(3000);
    }
}
