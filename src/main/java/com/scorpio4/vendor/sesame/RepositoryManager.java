package com.scorpio4.vendor.sesame;
/*
 *   Scorpio4 - Apache Licensed
 *   Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *
 */

import com.scorpio4.util.Identifiable;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.SystemRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.federation.Federation;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.inferencer.fc.config.ForwardChainingRDFSInferencerConfig;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.config.MemoryStoreConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;

/**
 * Scorpio4 (c) 2010-2013
 * @author lee
 * Date: 18/01/13
 * Time: 7:07 PM
 * <p/>
 * This code does something useful
 */
public class RepositoryManager extends LocalRepositoryManager implements Identifiable {
	private static final Logger log = LoggerFactory.getLogger(RepositoryManager.class);

	public RepositoryManager(File home) throws RepositoryException, MalformedURLException {
		super(home);
		initialize();
		SystemRepository systemRepository = createSystemRepository();

		log.debug("SystemRepository() " + systemRepository+" @ "+systemRepository.isInitialized()+" & "+ systemRepository.isWritable());
	}

	public Repository getRepository(String repositoryId) throws RepositoryException, RepositoryConfigException {
		Repository repository = super.getRepository(repositoryId);
		if (repository!=null) {
			log.debug("getRepository() "+repositoryId);
			return repository;
		}
		return getNewRepository(repositoryId);
	}

	private Repository getNewRepository(String repositoryId) throws RepositoryException, RepositoryConfigException {
		log.debug("getNewRepository() "+repositoryId);
		RepositoryConfig repositoryConfig = newDiskRepositoryConfig(repositoryId, true);
		addRepositoryConfig(repositoryConfig);
		return super.getRepository(repositoryId);
	}

	public Repository createRepository(String repositoryId) throws RepositoryException, RepositoryConfigException {
		log.debug("createRepository() "+repositoryId);
		Repository repository = super.createRepository(repositoryId);
		if (repository!=null) return repository;
		return null;
	}

	public Repository createRepository(boolean infer) {
		if (infer)
			return new SailRepository(new ForwardChainingRDFSInferencer( new MemoryStore() ) );
		else
			return new SailRepository( new MemoryStore() );
	}

	public Federation getFederation(String repositoryId) {
		return getFederation(repositoryId, null);
	}

	public Federation getFederation(String repositoryId, Collection<Repository> repositories) {
		log.debug("getFederation() "+repositoryId+" -> "+repositories);
		Federation federation = new Federation();
		federation.setDistinct(true);
		if (repositories==null || repositories.isEmpty()) return federation;

		for(Repository repo: repositories) {
			federation.addMember(repo);
		}
		return federation;
	}

	public RepositoryConfig newMemoryRepositoryConfig(String repositoryId, boolean persist, boolean infer) {
		SailImplConfig backendConfig = new MemoryStoreConfig(persist);
		if (infer) {
			backendConfig = new ForwardChainingRDFSInferencerConfig(backendConfig);
		}
		log.debug("newMemoryRepository() "+repositoryId+" -> "+backendConfig);
		SailRepositoryConfig repositoryTypeSpec = new SailRepositoryConfig(backendConfig);
		return new RepositoryConfig(repositoryId, repositoryTypeSpec);
	}

	public RepositoryConfig newDiskRepositoryConfig(String repositoryId, boolean infer) {
		SailImplConfig backendConfig = new NativeStoreConfig();
		if (infer) {
			backendConfig = new ForwardChainingRDFSInferencerConfig(backendConfig);
		}
		log.debug("newDiskRepository() "+repositoryId+" -> "+backendConfig);
		SailRepositoryConfig repositoryTypeSpec = new SailRepositoryConfig(backendConfig);
		return new RepositoryConfig(repositoryId, repositoryTypeSpec);
	}

	@Override
	public String getIdentity() {
		try {
			return getLocation().toExternalForm();
		} catch (MalformedURLException e) {
			return "file://"+getBaseDir().getAbsolutePath();
		}
	}
}
