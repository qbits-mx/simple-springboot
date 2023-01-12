package mx.qbits.publisher.access.api.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.qbits.publisher.access.api.model.domain.Registro;
import mx.qbits.publisher.access.api.model.domain.Response;
import mx.qbits.publisher.access.api.service.ClaveSrv;

@RestController
@RequestMapping
public class ClaveController {
    private ClaveSrv claveSrv;

    public ClaveController(ClaveSrv claveSrv) {
        this.claveSrv = claveSrv;
    }

    @PostMapping(
            value = "/generate",
            produces = "application/json; charset=utf-8")
    public Response generate(@RequestBody Registro reg) throws Exception {
        return claveSrv.generaClave(reg);
    }

    @GetMapping(
            value = "/reset/{id}",
            produces = "application/json; charset=utf-8")
    public Response reset(@PathVariable("id") String id) throws Exception {
        return claveSrv.reset(id);
    }

    @GetMapping(
            value = "/version",
            produces = "application/json; charset=utf-8")
    public String getVersion() throws Exception {
        return "{'backend-version':'1.0.7'}".replace('\'', '\"');
    }
}
