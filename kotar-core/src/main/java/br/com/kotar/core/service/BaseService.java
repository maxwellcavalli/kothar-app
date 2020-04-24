package br.com.kotar.core.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.sf.ehcache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.component.Messages;
import br.com.kotar.core.domain.BaseDomain;
import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.util.hql.helper.Join;
import br.com.kotar.core.util.hql.helper.ParsedObject;
import br.com.kotar.core.util.hql.parser.HqlParser;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public abstract class BaseService<T extends BaseDomain> {

    //@formatter:off
    @Autowired
    protected EntityManager em;
    @Autowired
    protected Messages messages;
    @Autowired
    protected CacheManager cacheManager;
    //@formatter:on

    public abstract BaseRepository<T> getRepository();

    public void beforeSave(T t) throws Exception {

    }

    public T afterSave(T t) {
        return t;
    }

    public Optional<T> findById(Long id) {
        return getRepository().findById(id);
    }

    public T saveOrUpdate(T entity) throws Exception {
        beforeSave(entity);
        T t = getRepository().save(entity);
        return afterSave(t);
    }

    public <S extends T> S save(S entity) {
        return getRepository().save(entity);
    }

    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        return getRepository().saveAll(entities);
    }

//	public T findOne(Long id) {
//		return (T) getRepository().findOne(id);
//	}

//	public boolean exists(Long id) {
//		return getRepository().exists(id);
//	}

    public Iterable<T> findAll() {
        return (Iterable<T>) getRepository().findAll();
    }

    public Iterable<T> findAll(Iterable<Long> ids) {
        return (Iterable<T>) getRepository().findAllById(ids);
    }

    public long count() {
        return getRepository().count();
    }

    public void delete(Long id) {
        getRepository().deleteById(id);
    }

    public void delete(T entity) throws Exception {
        getRepository().delete(entity);
    }

    public void delete(Iterable<? extends T> entities) {
        getRepository().deleteAll(entities);
    }

    public void deleteAll() {
        getRepository().deleteAll();
    }

    protected Page<T> searchPaginated(String hql, Pageable pageable, Map<String, Object> parameters) throws Exception {
        if (hql == null) {
            throw new IllegalArgumentException("HQL de Pesquisa Inválido");
        }

        ParsedObject parsedObject = HqlParser.parse(hql);
        String clazzName = getTypeClassName().substring(0, 1).toLowerCase() + getTypeClassName().substring(1);

        StringBuilder hqlCount = new StringBuilder();
        hqlCount.append(" select count(1) ");
        hqlCount.append(" from ");
        hqlCount.append(getTypeCanonicalName() + " " + clazzName);
        hqlCount.append(" ");

        for (Join join : parsedObject.getListJoin()) {
            hqlCount.append(join.getJoinType()).append(" join ").append(" ");
            hqlCount.append(join.getJoinExpression()).append(" ");
            hqlCount.append(join.getAlias()).append("\n");
        }

        hqlCount.append("\n");
        hqlCount.append(parsedObject.getWhere().getWhereExpression());

        Long count = recordCount(hqlCount.toString(), parameters);
        List<T> list = new ArrayList<>();

        if (count > 0) {
            list = search(hql, pageable.getPageSize(), pageable.getPageNumber(), parameters);
        }

        return new PageImpl<>(list, pageable, count);
    }

    public Long recordCount(String hql, Map<String, Object> parameters) throws Exception {
        Object retorno = loadObject(hql, parameters);
        Long recordCount = (Long) retorno;
        return recordCount;
    }

    private String getTypeClassName() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType p = (ParameterizedType) t;
        Class<?> clazz = (Class<?>) p.getActualTypeArguments()[0];
        String className = clazz.getSimpleName();

        return className;
    }

    private String getTypeCanonicalName() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType p = (ParameterizedType) t;
        Class<?> clazz = (Class<?>) p.getActualTypeArguments()[0];
        String className = clazz.getCanonicalName();

        return className;
    }

    protected Object loadObject(String hql, Map<String, Object> parameters) throws Exception {
        Query query = em.createQuery(hql);
        parseQueryParameters(query, parameters);
        return query.getSingleResult();
    }

    protected void parseQueryParameters(Query query, Map<String, Object> parameters) {
        for (String k : parameters.keySet()) {
            Object paramValue = parameters.get(k);
            query.setParameter(k, paramValue);
        }
    }

    @SuppressWarnings("unchecked")
    protected List<T> search(String hql, Integer pageSize, Integer page, Map<String, Object> parameters) throws Exception {
        Query query = em.createQuery(hql);

        query.setFirstResult((page * pageSize));
        query.setMaxResults(pageSize);
        parseQueryParameters(query, parameters);
        return query.getResultList();
    }

    public Iterable<T> findAll(Sort sort) {
        return getRepository().findAll();
    }

    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

}
