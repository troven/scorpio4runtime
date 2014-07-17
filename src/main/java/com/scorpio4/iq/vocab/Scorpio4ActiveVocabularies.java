package com.scorpio4.iq.vocab;

import com.scorpio4.runtime.ExecutionEnvironment;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.iq
 * User  : lee
 * Date  : 3/07/2014
 * Time  : 11:47 PM
 */
public class Scorpio4ActiveVocabularies extends AbstractActiveVocabulary {
	final Logger log = LoggerFactory.getLogger(this.getClass());
	ActiveBeansVocabulary springBeans;
	ActiveFLOVocabulary flo;
	ASQVocabulary asq;

	public Scorpio4ActiveVocabularies(ExecutionEnvironment engine) throws Exception {
		super(engine.getIdentity(), engine, false);
	}

	public void boot(ExecutionEnvironment engine) throws Exception {
		this.springBeans = new ActiveBeansVocabulary(engine);
		this.flo = new ActiveFLOVocabulary(engine);
		this.asq = new ASQVocabulary(engine);

		log.debug("Booted Active Vocabularies: "+engine.getIdentity());
	}

	@Override
	public void start() throws Exception {
		asq.start();
		springBeans.start();
		flo.start();
	}

	@Override
	public void stop() throws Exception {
		flo.stop();
		springBeans.stop();
		asq.stop();
	}

	public Object activate(String triggerURI, Object body) {
		try {
			return flo.activate(triggerURI, body);
		} catch (Exception e) {
			log.warn("Faulty Trigger: "+triggerURI+" ->"+e.getCause().getMessage());
			return null;
		}
	}

	public CamelContext getCamelContext() {
		return flo.getCamelContext();
	}

	public void startAndWait() throws Exception {
		start();
		log.debug("Waiting until FLOs are active ...");
		while ( getCamelContext().isStartingRoutes()) {
			Thread.sleep(100);
		}
		log.debug("Active Vocabularies are ready.");
	}
}
