package br.com.challenge.infrastructure.config;

import br.com.challenge.domain.model.Apoiador;
import br.com.challenge.domain.model.Paciente;
import br.com.challenge.domain.model.Usuario;
import br.com.challenge.infrastructure.api.rest.dto.input.ApoiadorInputDto;
import br.com.challenge.infrastructure.api.rest.dto.input.PacienteInputDto;
import br.com.challenge.infrastructure.api.rest.dto.input.UsuarioInputDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class ApplicationConfig {

    @ConfigProperty(name = "app.name", defaultValue = "Challenge Health API")
    String appName;

    @ConfigProperty(name = "app.version", defaultValue = "1.0.0")
    String appVersion;

    @ConfigProperty(name = "app.environment", defaultValue = "development")
    String environment;

    @Produces
    @Singleton
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true);

        configurarMapeamentosEspecificos(modelMapper);

        System.out.println("-----CONFIGURADO COM MAPEAMENTOS DE HERANÇA-----");

        return modelMapper;
    }

    /**
     * Configura mapeamentos específicos para resolver o problema de herança
     */
    private void configurarMapeamentosEspecificos(ModelMapper modelMapper) {
        try {
            modelMapper.createTypeMap(ApoiadorInputDto.class, Apoiador.class)
                    .addMappings(mapper -> {
                        // Campos da classe base Usuario
                        mapper.map(ApoiadorInputDto::getNomeCompleto, Apoiador::setNomeCompleto);
                        mapper.map(ApoiadorInputDto::getIdade, Apoiador::setIdade);
                        mapper.map(ApoiadorInputDto::getCpf, Apoiador::setCpf);
                        mapper.map(ApoiadorInputDto::getCep, Apoiador::setCep);
                        mapper.map(ApoiadorInputDto::getNumero, Apoiador::setNumero);
                        mapper.map(ApoiadorInputDto::getComplemento, Apoiador::setComplemento);
                        mapper.map(ApoiadorInputDto::getTelefone, Apoiador::setTelefone);
                        mapper.map(ApoiadorInputDto::getSenha, Apoiador::setSenha);
                        // Campos específicos do Apoiador
                        mapper.map(ApoiadorInputDto::getCargo, Apoiador::setCargo);
                        mapper.map(ApoiadorInputDto::getAreaAtuacao, Apoiador::setAreaAtuacao);
                    });

            modelMapper.createTypeMap(PacienteInputDto.class, Paciente.class)
                    .addMappings(mapper -> {
                        mapper.map(PacienteInputDto::getNomeCompleto, Paciente::setNomeCompleto);
                        mapper.map(PacienteInputDto::getIdade, Paciente::setIdade);
                        mapper.map(PacienteInputDto::getCpf, Paciente::setCpf);
                        mapper.map(PacienteInputDto::getCep, Paciente::setCep);
                        mapper.map(PacienteInputDto::getNumero, Paciente::setNumero);
                        mapper.map(PacienteInputDto::getComplemento, Paciente::setComplemento);
                        mapper.map(PacienteInputDto::getTelefone, Paciente::setTelefone);
                        mapper.map(PacienteInputDto::getSenha, Paciente::setSenha);
                        mapper.map(PacienteInputDto::getTelefoneContato, Paciente::setTelefoneContato);
                        mapper.map(PacienteInputDto::getNumeroSusOuConvenio, Paciente::setNumeroSusOuConvenio);
                        mapper.map(PacienteInputDto::getApoiadorId, Paciente::setApoiadorId);
                    });

            System.out.println("MAPEAMENTOS DE HERANÇA CONFIGURADOS COM EXITO");

        } catch (Exception e) {
            System.err.println("ERRO AO CONFIGURAR MAPEAMENTOS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Produces
    @Singleton
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        return mapper;
    }

    @Produces
    @Singleton
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    public String getAppInfo() {
        return String.format("%s v%s - %s", appName, appVersion, environment);
    }

    public String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}