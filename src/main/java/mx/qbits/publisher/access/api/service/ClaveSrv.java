package mx.qbits.publisher.access.api.service;

import mx.qbits.publisher.access.api.model.domain.Registro;
import mx.qbits.publisher.access.api.model.domain.Response;

public interface ClaveSrv {
    Response generaClave(Registro r) throws Exception;
    Response reset(String id) throws Exception;
}
