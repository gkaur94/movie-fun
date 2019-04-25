package org.superbiz.moviefun.movies;

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
public class MoviesConfiguration {

    @Bean(name = "moviesDataSource")
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(serviceCredentials.jdbcUrl("movies-mysql"));
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "moviesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean moviesContainerEntityManagerFactoryBean(@Qualifier("moviesDataSource") DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        entityManagerFactoryBean.setPersistenceUnitName("movie-unit");
        return entityManagerFactoryBean;
    }

    @Bean(name = "moviesTransactionManager")
    public PlatformTransactionManager moviesTransactionManager(@Qualifier("moviesEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
