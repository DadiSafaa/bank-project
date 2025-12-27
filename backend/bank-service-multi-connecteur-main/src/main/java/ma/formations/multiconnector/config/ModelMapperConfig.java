package ma.formations.multiconnector.config;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.common.CommonTools;
import ma.formations.multiconnector.service.exception.BusinessException;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.util.Date;

@Configuration
@AllArgsConstructor
public class ModelMapperConfig {
    private CommonTools tools;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Convertisseurs existants pour les dates
        Converter<Date, String> dateToStringConverter = new AbstractConverter<>() {
            @Override
            protected String convert(Date date) {
                return tools.dateToString(date);
            }
        };
        Converter<String, Date> stringToDateConverter = new AbstractConverter<>() {
            @Override
            protected Date convert(String s) {
                try {
                    return tools.stringToDate(s);
                } catch (ParseException e) {
                    throw new BusinessException(String.format("the date %s doesn't respect the format %s", s, tools.getDateFormat()));
                }
            }
        };

        // Ajout du nouveau convertisseur String à Boolean
        Converter<String, Boolean> stringToBooleanConverter = new AbstractConverter<>() {
            @Override
            protected Boolean convert(String source) {
                // Implémentez la logique de conversion ici
                // Par exemple, retourne true si la chaîne est "A100", sinon false
                return "A100".equals(source);
            }
        };

        // Ajouter les convertisseurs au ModelMapper
        modelMapper.addConverter(dateToStringConverter);
        modelMapper.addConverter(stringToDateConverter);
        modelMapper.addConverter(stringToBooleanConverter);

        return modelMapper;
    }
}
