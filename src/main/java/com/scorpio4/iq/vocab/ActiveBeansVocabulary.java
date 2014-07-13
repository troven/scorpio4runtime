package com.scorpio4.iq.vocab;

import com.scorpio4.fact.FactSpace;
import com.scorpio4.runtime.ExecutionEnvironment;
import com.scorpio4.vendor.sesame.crud.SesameCRUD;
import com.scorpio4.vendor.spring.RDFBeanDefinitionReader;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Map;

/**
 * scorpio4-oss (c) 2014
 * Module: com.scorpio4.iq
 * User  : lee
 * Date  : 7/07/2014
 * Time  : 8:41 PM
 */
public class ActiveBeansVocabulary implements ActiveVocabulary {
	final Logger log = LoggerFactory.getLogger(this.getClass());

	RDFBeanDefinitionReader rdfBeanReader;


	public ActiveBeansVocabulary() {
	}

	public ActiveBeansVocabulary(ExecutionEnvironment engine) throws Exception {
		boot(engine);
	}

	@Override
	public void boot(ExecutionEnvironment engine) throws Exception {
		// Engine's dependencies
		log.debug("Active Beans Vocabulary");

		ApplicationContext registry = engine.getRegistry();

		FactSpace factSpace = new FactSpace(engine.getIdentity(), engine.getRepository());
		RepositoryConnection connection = factSpace.getConnection();
		rdfBeanReader = new RDFBeanDefinitionReader(connection, (BeanDefinitionRegistry)registry);

		SesameCRUD crud = new SesameCRUD(factSpace);
		Collection<Map> prototypes = crud.read("self/prototypes.sparql", engine.getConfig());
		for(Map bean:prototypes) {
			String beanURI = (String)bean.get("this");
			try {
				rdfBeanReader.loadBeanDefinitions(beanURI);
			} catch(Exception e) {
				log.error("Bean: "+beanURI);
			}
		}
		log.debug("Registered "+prototypes.size()+" Prototypes");

		Collection<Map> singletons = crud.read("self/singletons.sparql", engine.getConfig());
		for(Map bean:singletons) {
			String beanURI = (String)bean.get("this");
			try {
				rdfBeanReader.loadBeanDefinitions(beanURI);
			} catch(Exception e) {
				log.error("Bean: "+beanURI);
			}
		}
		log.debug("Registered "+singletons.size()+" Singletons");

		connection.close();
	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void stop() throws Exception {

	}

	@Override
	public Object activate(String resource, Object body) {
		log.warn("Bean Trigger not supported: "+resource);
		return rdfBeanReader.getBean(resource);
	}
}
