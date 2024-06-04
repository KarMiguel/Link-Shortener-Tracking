package io.github.karMiguel.capzip.dtos.mapper;


import io.github.karMiguel.capzip.dtos.RegisterUserDto;
import io.github.karMiguel.capzip.model.User;
import org.modelmapper.ModelMapper;

public class UserMapper {

    private static ModelMapper mapper = new ModelMapper();

    public static User toUser(RegisterUserDto dto){
        return  mapper.map(dto, User.class);
    }

}
