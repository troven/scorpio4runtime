package com.scorpio4.vendor.camel.self;

import com.scorpio4.runtime.ExecutionEnvironment;
import com.scorpio4.iq.exec.SPARQLing;

import java.io.IOException;

/**
 * Scorpio (c) 2014
 * Module: com.scorpio4.vendor.camel.component.asset
 * User  : lee
 * Date  : 23/06/2014
 * Time  : 3:28 AM
 */
public class SPARQL extends Execute {

	public SPARQL(ExecutionEnvironment engine, String uri) throws IOException {
		super(engine,uri, new SPARQLing(engine.getFactSpace()));
	}

}
