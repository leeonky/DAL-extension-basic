package com.github.leeonky.dal.extensions.basic.sftp.util;

import com.github.leeonky.dal.runtime.Data;
import com.github.leeonky.dal.runtime.inspector.Dumper;
import com.github.leeonky.dal.runtime.inspector.DumpingContext;

public class DirDumper implements Dumper {

    @Override
    public void dumpDetail(Data data, DumpingContext context) {
        DumpingContext sub = context.append(((SFtpFile) data.getInstance()).remoteInfo()).sub();
        data.getDataList().forEach(subFile -> sub.newLine().dump(subFile));
    }

    @Override
    public void dump(Data data, DumpingContext context) {
        DumpingContext sub = context.append(((SFtpFile) data.getInstance()).name()).append("/").indent();
        data.getDataList().forEach(subFile -> sub.newLine().dump(subFile));
    }
}