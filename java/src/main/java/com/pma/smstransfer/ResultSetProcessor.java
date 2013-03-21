package com.pma.smstransfer;

import java.sql.ResultSet;

public interface ResultSetProcessor {

    void process(ResultSet resultSet) throws Exception;
}
