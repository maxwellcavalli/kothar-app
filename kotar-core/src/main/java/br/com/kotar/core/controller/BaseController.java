package br.com.kotar.core.controller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.kotar.core.component.Messages;
import br.com.kotar.core.domain.BaseDomain;
import br.com.kotar.core.helper.datatable.PageFilter;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.core.util.ObjectUtil;

public abstract class BaseController<T extends BaseDomain> {

    //@formatter:off
    @Autowired
    protected Messages messages;
    @Autowired
    protected HttpServletRequest request;
    //@formatter:on

    public abstract BaseService<T> getService();

    public abstract void beforeSave(T t);

    public abstract void validationBeforeSave(T t) throws Exception;

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseHelper<T>> getDomain(@PathVariable("id") long id) throws Exception {
        try {
            System.out.println("Fetching Domain with id " + id);
            Optional<T> tOptional = onGet(id);

            if (tOptional.isPresent()) {
                System.out.println("Domain with id " + id + " not found");
                return new ResponseEntity<ResponseHelper<T>>(HttpStatus.NOT_FOUND);
            }

            T domain = tOptional.get();
            return new ResponseEntity<ResponseHelper<T>>(new ResponseHelper<T>(domain), HttpStatus.OK);
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            return new ResponseEntity<ResponseHelper<T>>(new ResponseHelper<T>(message), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ResponseHelper<T>> createDomain(@RequestBody T domain, UriComponentsBuilder ucBuilder) {
        try {
            System.out.println("Creating domain " + domain.toString());
            validationBeforeSave(domain);
            beforeSave(domain);

            domain = onSave(domain);

            return new ResponseEntity<ResponseHelper<T>>(new ResponseHelper<T>(domain), HttpStatus.OK);
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            return new ResponseEntity<ResponseHelper<T>>(new ResponseHelper<T>(message), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseHelper<T>> updateDomain(@PathVariable("id") long id, @RequestBody T domain) {
        try {
            System.out.println("Updating Domain " + id);

            validationBeforeSave(domain);
            beforeSave(domain);
            domain.setId(id);

            domain = onUpdate(domain);

            return new ResponseEntity<ResponseHelper<T>>(new ResponseHelper<T>(domain), HttpStatus.OK);
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            e.printStackTrace();

            return new ResponseEntity<ResponseHelper<T>>(new ResponseHelper<T>(message), HttpStatus.EXPECTATION_FAILED);
        }
    }

    protected Page<T> onSearch(PageFilter pageFilter) throws Exception {
        throw new NotImplementedException();
    }

    protected Optional<T> onGet(Long id) throws Exception {
        return getService().findById(id);
    }

    protected T onSave(T domain) throws Exception {
        return getService().save(domain);
    }

    protected T onUpdate(T domain) throws Exception {
        return getService().save(domain);
    }

    protected void onDelete(T domain, long id) throws Exception {
        getService().delete(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ResponseHelper<T>> deleteDomain(@PathVariable("id") long id) {
        try {
            System.out.println("Fetching & Deleting Domain with id " + id);
            Optional<T> tOptional = onGet(id);

            if (!tOptional.isPresent()) {
                String message = "Unable to delete. Domain with id " + id + " not found";
                System.out.println(message);

                return new ResponseEntity<ResponseHelper<T>>(new ResponseHelper<T>(message), HttpStatus.NOT_FOUND);
            }

            T domain = tOptional.get();

            onDelete(domain, id);
            // getRepository().delete(id);
            return new ResponseEntity<ResponseHelper<T>>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            return new ResponseEntity<ResponseHelper<T>>(new ResponseHelper<T>(message), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseHelper<Page<T>>> search(@RequestBody PageFilter pageFilter) {

        try {
            String filter = "%%";
            if (pageFilter.getFilterValue() != null) {
                if (pageFilter.getFilterValue() instanceof String) {
                    filter = (String) pageFilter.getFilterValue();

                    if (filter.indexOf("%") == -1) {
                        filter = "%" + filter + "%";
                    }
                    pageFilter.setFilterValue(filter);
                }
            }


            Page<T> paged = onSearch(pageFilter);
            ResponseHelper<Page<T>> retorno = new ResponseHelper<Page<T>>(paged);
            return new ResponseEntity<ResponseHelper<Page<T>>>(retorno, HttpStatus.OK);
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            return new ResponseEntity<ResponseHelper<Page<T>>>(new ResponseHelper<Page<T>>(message), HttpStatus.EXPECTATION_FAILED);
        }
    }

    protected Pageable getPageable(PageFilter pageFilter) {
        int page = pageFilter.getPage().getPageNumber();
        int size = pageFilter.getPage().getSize();

        Sort sort = null;

        if (pageFilter.getSort() != null && pageFilter.getSort().getPropertyName() != null
                && !pageFilter.getSort().getPropertyName().trim().isEmpty()) {

            Direction direction = Direction.DESC;
            if (pageFilter.getSort().getOrderType() != null && pageFilter.getSort().getOrderType().equals("asc")) {
                direction = Direction.ASC;
            }

            String propertyName = pageFilter.getSort().getPropertyName();
            String tmp = getPropertyName(propertyName);
            if (tmp != null && !tmp.isEmpty()) {
                propertyName = tmp;
            }

            sort = Sort.by(direction, propertyName);
        }

        if (sort != null) {
            return PageRequest.of(page, size, sort);
        } else {
            return PageRequest.of(page, size);
        }
    }

    private String getPropertyName(String propertyName) {

        try {
            boolean propertyByMethod = false;
            Class<?> clazz = (Class<?>) ((java.lang.reflect.ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Object obj = clazz.newInstance();
            Method[] methods = obj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(JsonProperty.class)) {
                    JsonProperty jsonProperty = method.getAnnotation(JsonProperty.class);
                    if (jsonProperty.value().equalsIgnoreCase(propertyName)) {
                        String methodName = method.getName();
                        methodName = methodName.substring(3);
                        methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

                        propertyName = methodName;
                        propertyByMethod = true;
                        break;
                    }
                }
            }

            if (!propertyByMethod) {

                if (propertyName.indexOf(".") > -1) {
                    String[] fa = propertyName.split("\\.");
                    StringBuilder builder = new StringBuilder();
                    for (String f : fa) {
                        Object[] ret = ObjectUtil.getPropertyName(f, obj);

                        builder.append(ret[0]);
                        builder.append(".");
                        obj = ret[1];
                    }

                    propertyName = builder.toString();
                    propertyName = propertyName.substring(0, propertyName.length() - 1);

                } else {

                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(JsonProperty.class)) {

                            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                            if (jsonProperty.value().equalsIgnoreCase(propertyName)) {
                                propertyName = field.getName();

                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return propertyName;
    }

    protected Object filterValueToObject(Object filterValue, Class<?> clazz) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        String json = objectMapper.writeValueAsString(filterValue);

        return objectMapper.readValue(json, clazz);
    }

}
