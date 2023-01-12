package mx.qbits.publisher.access.api.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import mx.qbits.publisher.access.api.mapper.StoreDB;
import mx.qbits.publisher.access.api.model.domain.EmailMessageRequest;
import mx.qbits.publisher.access.api.model.domain.EmailMessageResponse;
import mx.qbits.publisher.access.api.model.domain.Par;
import mx.qbits.publisher.access.api.model.domain.Registro;
import mx.qbits.publisher.access.api.model.domain.Response;
import mx.qbits.publisher.access.api.model.domain.SmsMessageRequest;
import mx.qbits.publisher.access.api.utils.Generator;

@Slf4j
@Service
public class ClaveSrvImpl implements ClaveSrv {
    private final RestTemplate restTemplate;

    @Value("${db_url}")
    private String url;
    @Value("${db_user}")
    private String user;
    @Value("${db_pass}")
    private String pass;
    @Value("${db_driver}")
    private String driver;
    @Value("${app.mail.server}")
    private String appMailServer;

    public ClaveSrvImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Response generaClave(Registro r) {
        try {
            if(r.getCorreo().endsWith(".ja")) throw new Exception("Termina con Japón");
            return gen(r);
        } catch (Exception e) {
            Response response = new Response();
            response.setError(e.toString());
            Map<String, String> map = new HashMap<>();
            map.put("error", e.toString());
            sendMail("plantilla.html","Error de artemisa","garellanos@ultrasist.com.mx", map);
            return response;
        }
    }

    private Response gen(Registro r) throws Exception {
        Response response = new Response();
        String val = valida(r);
        if(val.length()>0) {
            response.setError("Datos incorrectos: " + val);
            return response;
        }

        StoreDB st = new StoreDB(driver, url, user, pass);
        if(st.registrado(r.getCorreo())) {
            response.setError("El registro sólo se permite una sola vez");
            st.done();
            return response;
        }

        response = Generator.generaClave();

        Par par = st.obtenId(response.getOlderThan());
        if(par.getId()>0) {
            response.setUser(par.getUsername());
            st.actualiza(response.getHashed(), par.getId());
            st.sicroniza(r, par.getId());
            st.done();
            this.sendMail(response, r);
            /**/
            String s = this.sendSms(r.getTelefono(),
                    "Gracias por tu registro !!! " +
                    "Ve a http://artemisa.ultrasist.net/login/index.php y entra "
                    + " con el usuario: " + response.getUser()
                    + " y el password: " + response.getGen());
            log.info(s);
            /**/
            return response;
        }
        st.done();
        response = new Response();
        response.setError("can not do the operation. id="+par.getId());
        return response;
    }

    private void sendMail(Response response, Registro registro) {
        log.info("sending mail");
        Map<String, String> map = new HashMap<>();
        map.put("%NOMBRE%", registro.getNombreCompleto());
        map.put("%USUARIO%", response.getUser());
        map.put("%CLAVE%", response.getGen());
        sendMail("template-artemisa-registry.html","Solicitud de registro ultrasist", registro.getCorreo(), map);
    }

    private String valida(Registro r) {
        log.info("validating");
        return "";
    }

    private EmailMessageResponse sendMail(
            String nombrePlantillaCorreo,
            String title,
            String to,
            Map<String, String> mapa) {
        EmailMessageRequest emr = new EmailMessageRequest();
        emr.setTemplate(nombrePlantillaCorreo);
        emr.setTitle(title);
        emr.setTo(to);
        emr.setValues(mapa);

        String endpoint = appMailServer + "api/send-mail";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<EmailMessageRequest> entity = new HttpEntity<>(emr, headers);
        return restTemplate.postForObject(endpoint, entity, EmailMessageResponse.class);
    }

    private String sendSms(String tel, String msg) {
        SmsMessageRequest m = new SmsMessageRequest();
        m.setTelefono(tel);
        m.setMensaje(msg);

        HttpHeaders headers = new HttpHeaders();
        String endpoint = "https://sms.qbits.mx/send";
        headers.add("credentials", "hola_mundo");
        HttpEntity<SmsMessageRequest> entity = new HttpEntity<>(m, headers);
        return restTemplate.postForObject(endpoint, entity, String.class);
    }

    @Override
    public Response reset(String id) throws Exception {
        Response r = new Response();
        r.setError("BAD-ID");
        StoreDB st = new StoreDB(driver, url, user, pass);
        if("UrbiEtOrbi1".equals(id)) {
            st.reset();
            r.setError("NO-ERROR");
            return r;
        }
        return r;
    }

}
