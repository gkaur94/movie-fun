package org.superbiz.moviefun.albums;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.superbiz.moviefun.DatabaseServiceCredentials;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class AlbumsConfiguration {

    @Bean(name = "albumsDataSource")
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(serviceCredentials.jdbcUrl("albums-mysql"));
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "albumsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(@Qualifier("albumsDataSource") DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        entityManagerFactoryBean.setPersistenceUnitName("album-unit");
        return entityManagerFactoryBean;
    }

    @Bean(name = "albumsTransactionManager")
    public PlatformTransactionManager albumsTransactionManager(@Qualifier("albumsEntityManagerFactory")EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
