package com.dayatang.hibernate;

import javax.persistence.EntityManager;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.dayatang.domain.AbstractEntity;
import com.dayatang.domain.InstanceFactory;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.dayatang.btm.BtmUtils;
import com.dayatang.configuration.Configuration;
import com.dayatang.configuration.ConfigurationFactory;
import com.dayatang.h2.H2Server;

public class AbstractIntegrationTest {

	private static BtmUtils btmUtils;
	
	protected static SessionFactory sessionFactory;

    private UserTransaction tx;

    protected EntityRepositoryHibernate repository;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Configuration configuration = new ConfigurationFactory().fromClasspath("/jdbc.properties");
		btmUtils = BtmUtils.readConfigurationFromClasspath("/datasources.properties");
		btmUtils.setupDataSource();
		sessionFactory = HibernateUtils.getSessionFactory();
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		sessionFactory.close();
		btmUtils.closeDataSource();
		btmUtils = null;
		System.out.println("================================================");
		System.out.println("关闭BTM");
	}

    @Before
    public void setUp() throws Exception {
        InstanceFactory.bind(SessionFactory.class, sessionFactory);
        repository = new EntityRepositoryHibernate();
        AbstractEntity.setRepository(repository);
        tx = getTransaction();
        tx.begin();
    }

    @After
    public void tearDown() throws IllegalStateException, SystemException {
        tx.rollback();
        repository = null;
        AbstractEntity.setRepository(null);
    }

	private UserTransaction getTransaction() {
		return btmUtils.getTransaction();
	}
	
}
