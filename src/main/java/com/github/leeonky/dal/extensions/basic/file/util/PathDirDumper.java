package com.github.leeonky.dal.extensions.basic.file.util;

import com.github.leeonky.dal.runtime.Data;
import com.github.leeonky.dal.runtime.inspector.Dumper;
import com.github.leeonky.dal.runtime.inspector.DumpingContext;

public class PathDirDumper implements Dumper {

    @Override
    public void dumpDetail(Data data, DumpingContext context) {
        DumpingContext sub = context.append("java.nio.Path").appendThen(" ").append(data.getInstance() + "/").sub();
        data.getDataList().forEach(subPath -> sub.newLine().dump(subPath)); //will dump in FileDirDumper or FileFileDumper
    }
}