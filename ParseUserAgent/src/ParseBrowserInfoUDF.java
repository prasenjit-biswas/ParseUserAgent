//package com.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

import eu.bitwalker.useragentutils.UserAgent;


public class ParseBrowserInfoUDF extends UDF {
	
	@Description(name = "testudf",
	value = "_FUNC_(input) - from the input date string "+
	"or separate month and day arguments, returns the sign of the Zodiac.",
	extended = "Example:\n"
	+ " > SELECT _FUNC_(input_string) FROM src;\n")
	
	
	static final String BROWSER_DT = "browserDt";
	static final String BROWSER_NAME = "browserName";
	static final String WEB_CRALLER = "Web Crawler";
	static final String REGEX = "(.*[^0-9.])";
	static final String UNKNOWN = "Unknown";
	
	public String evaluate(String agent, String type) {
		String result = null;
		String browserVersion ="";
		String browserName = "";
		try{
			UserAgent userAgent = UserAgent.parseUserAgentString(agent);
			
			if(userAgent != null && userAgent.getBrowser() != null){
				browserName = userAgent.getBrowser().getName();
			}
			if(userAgent != null && userAgent.getBrowserVersion() != null){
				browserVersion = userAgent.getBrowserVersion().getVersion();
			}
			System.out.println("browserName "+browserName);
			System.out.println("browserVersion "+browserVersion);
		
			
			if(browserName != null && !("").equals(browserName) && !("Unknown").equalsIgnoreCase(browserName)){
				Pattern pattern = Pattern.compile(REGEX);
				Matcher m = pattern.matcher(browserName);
				if(m.find()){
					browserName = m.group();
				}
			}
			
			if(BROWSER_NAME.equals(type)){
				if("Unknown".equalsIgnoreCase(browserName)){
					result = WEB_CRALLER;
				}else if(browserName != null && !("").equals(browserName)){
					result = browserName.trim();
				}else{
					result = UNKNOWN;
				}
			}else if(BROWSER_DT.equals(type)){
				if("Unknown".equalsIgnoreCase(browserName)){
					result = WEB_CRALLER;
				}else{
					result = getBrowserDt(browserName, browserVersion);
				}
			}
		}catch(Exception ex){
			ex.getMessage();
			ex.printStackTrace();
		}
		System.out.println(result);
		return result;	
	}
	
	
	public String getBrowserDt(String browserName, String browserVersion) throws Exception {
		String browserDt = "";
		if(browserName != null && !("").equals(browserName) ){
			if(browserVersion != null && !("").equals(browserVersion)){
				if(browserVersion.indexOf(".") != -1){
					browserVersion = browserVersion.substring(0,browserVersion.indexOf("."));
				}
				browserDt = browserName.trim()+"_Version:"+browserVersion.trim();
			}else{
				browserDt = browserName.trim();
			}
		}else{
			browserDt = UNKNOWN;
		}
		return browserDt;
	}
	
	
	public static void main(String[] args) {
		ParseBrowserInfoUDF parseBrowserInfoUDF = new ParseBrowserInfoUDF();
		parseBrowserInfoUDF.evaluate("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/4.0; MathPlayer 2.20; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E; IPH 1.1.21.4019)", "browserName");
		parseBrowserInfoUDF.evaluate("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/4.0; MathPlayer 2.20; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E; IPH 1.1.21.4019)", "browserDt");
	}
}
