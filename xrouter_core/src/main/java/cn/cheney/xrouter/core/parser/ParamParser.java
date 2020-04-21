package cn.cheney.xrouter.core.parser;

import java.lang.reflect.Type;

public interface ParamParser {

   Object parse(String paramName, String paramValue, Type paramType);

}
