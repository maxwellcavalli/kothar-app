package br.com.kotar.core.repository.impl;

import br.com.kotar.core.component.Messages;
import br.com.kotar.core.domain.BaseDomain;
import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.repository.TransformData;
import br.com.kotar.core.util.hql.helper.Join;
import br.com.kotar.core.util.hql.helper.ParsedObject;
import br.com.kotar.core.util.hql.parser.HqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseRepositoryImpl<T extends BaseDomain> implements BaseRepository<T> {

    //@formatter:off
    @Autowired
    protected EntityManager em;
    @Autowired
    protected Messages messages;
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

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return getRepository().saveAll(entities);
    }

    public boolean existsById(Long id) {
        return getRepository().existsById(id);
    }

    public Iterable<T> findAll() {
        return (Iterable<T>) getRepository().findAll();
    }

    public Iterable<T> findAllById(Iterable<Long> ids) {
        return (Iterable<T>) getRepository().findAllById(ids);
    }

    public long count() {
        return getRepository().count();
    }

    public void deleteById(Long id) {
        getRepository().deleteById(id);
    }

    public void delete(T entity) {
        getRepository().delete(entity);
    }

    public void deleteAll(Iterable<? extends T> entities) {
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

    protected String getTypeClassName() {
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

    protected <S extends T> Page<S> searchNativeQuery(StringBuilder sql_fields, StringBuilder sql_common, StringBuilder sql_order, Pageable pageable,
                                                      Map<String, Object> parameters, Class<S> clazz, TransformData<S> transformData) throws Exception {

        StringTokenizer stringTokenizer = new StringTokenizer(sql_common.toString(), " ");
        List<String> tokens = new ArrayList<>();
        while (stringTokenizer.hasMoreTokens()) {
            tokens.add(stringTokenizer.nextToken());
        }

        Map<String, String> tableAlias = new HashMap<>();

        int i = 0;
        while (i < tokens.size()) {
            String token = tokens.get(i);
            boolean isFrom = token.equalsIgnoreCase("from");
            boolean isJoin = token.equalsIgnoreCase("join");
            boolean isTableJoin = isJoin && tokens.get(i + 1).indexOf("(") > -1;

            if (isFrom || isJoin && !isTableJoin) {
                String tblName = tokens.get(++i);
                String tblAlias = tokens.get(++i);

                tableAlias.put(tblName.toLowerCase(), tblAlias);
            } else {
                i++;
            }
        }

        List<br.com.kotar.core.helper.datatable.Sort> sorts = new ArrayList<>();

        if (pageable.getSort() != null) {
            pageable.getSort().forEach(el -> {
                br.com.kotar.core.helper.datatable.Sort sort = new br.com.kotar.core.helper.datatable.Sort();
                sort.setPropertyName(el.getProperty());
                sort.setOrderType(el.getDirection().name());

                sorts.add(sort);
            });
        }

        if (!sorts.isEmpty()) {
            sql_order = new StringBuilder();
            sql_order.append("  order by ");
            sql_order.append(createDynamicOrderBy(sorts, tableAlias, clazz));
        }

        StringBuilder sql_count = new StringBuilder();
        sql_count.append(" select count(1) ");

        Query query = em.createNativeQuery(sql_count.toString() + sql_common.toString());
        for (String key : parameters.keySet()) {
            query.setParameter(key, parameters.get(key));
        }

        BigInteger count = (BigInteger) query.getSingleResult();
        List<S> retorno = new ArrayList<>();
        if (count.longValue() > 0) {
            query = em.createNativeQuery(sql_fields.toString() + sql_common.toString() + sql_order.toString());
            for (String key : parameters.keySet()) {
                query.setParameter(key, parameters.get(key));
            }

            Integer page = pageable.getPageNumber();
            Integer pageSize = pageable.getPageSize();

            query.setFirstResult((page * pageSize));
            query.setMaxResults(pageSize);

            List<?> list = query.getResultList();
            for (Object row : list) {
                Object[] col = (Object[]) row;
                S transformed = transformData.transform(col);
                retorno.add(transformed);
            }
        }

        return new PageImpl<S>(retorno, pageable, count.intValue());
    }

    private String retornaJoinHierarquia(String fieldName, Map<String, String> tableAlias, Class<?> clazz) throws Exception {
        StringBuilder sql_order = new StringBuilder();

        Object objeto = clazz.newInstance();

        Field[] tmpF = objeto.getClass().getDeclaredFields();
        List<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(tmpF));

        if (objeto.getClass().getSuperclass() != null) {
            Field[] fSuper = objeto.getClass().getSuperclass().getDeclaredFields();
            fields.addAll(Arrays.asList(fSuper));
        }

        List<String> sFields = Arrays.asList(fieldName.split("\\."));
        Class<?> clazzTable = null;

        for (int i = 0; i < sFields.size(); i++) {
            String s = sFields.get(i);
            List<Field> tmp = fields.stream().filter(el -> el.getName().equalsIgnoreCase(s)).collect(Collectors.toList());
            if (!tmp.isEmpty()) {
                Field field = tmp.get(0);
                field.setAccessible(true);
                Class<?> clazzTmp = field.getType();
                objeto = clazzTmp.newInstance();

                if (i + 2 == sFields.size()) {
                    clazzTable = clazzTmp;
                    break;
                }
            }
        }

        if (clazzTable != null) {
            String fName = sFields.get(sFields.size() - 1);
            List<Field> lFields = new ArrayList<>();
            lFields.addAll(Arrays.asList(clazzTable.getDeclaredFields()));

            if (clazzTable.getSuperclass() != null) {
                Field[] fSuper = clazzTable.getSuperclass().getDeclaredFields();
                lFields.addAll(Arrays.asList(fSuper));
            }

            List<Field> tmp = lFields.stream().filter(el -> el.getName().equalsIgnoreCase(fName)).collect(Collectors.toList());
            Field f = tmp.get(0);

            Table table = clazzTable.getAnnotation(Table.class);
            String tableName = table.name();

            Column column = f.getAnnotation(Column.class);
            String columnName = column.name();

            String alias = tableAlias.get(tableName);

            sql_order.append(alias + "." + columnName);
        }

        return sql_order.toString();
    }

    private String createDynamicOrderBy(List<br.com.kotar.core.helper.datatable.Sort> sorts, Map<String, String> tableAlias, Class<?> clazz) throws Exception {
        StringBuilder sql_order = new StringBuilder();

        Object objeto = clazz.newInstance();

        Field[] tmpF = objeto.getClass().getDeclaredFields();
        List<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(tmpF));

        if (objeto.getClass().getSuperclass() != null) {
            Field[] fSuper = objeto.getClass().getSuperclass().getDeclaredFields();
            fields.addAll(Arrays.asList(fSuper));
        }

        sorts.forEach(sort -> {
            String name = sort.getPropertyName();
            if (name.indexOf(".") > -1) {
                try {
                    String ret = retornaJoinHierarquia(name, tableAlias, clazz);

                    sql_order.append(ret + " " + sort.getOrderType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String tblCollumnName = "";
                for (Field f : fields) {
                    if (f.getName().equals(name)) {
                        Column column = f.getAnnotation(Column.class);
                        tblCollumnName = column.name();
                    }
                }

                if (!tblCollumnName.isEmpty()) {
                    String alias = "";
                    if (objeto.getClass().isAnnotationPresent(Table.class)) {
                        Table table = objeto.getClass().getAnnotation(Table.class);
                        String mappedTableName = table.name().toLowerCase();
                        alias = tableAlias.get(mappedTableName);
                    }

                    sql_order.append(alias + "." + tblCollumnName + " " + sort.getOrderType());
                }
            }
        });

        return sql_order.toString();
    }

}
