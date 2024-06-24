package io.github.karMiguel.capzip.dtos.mapper;


import io.github.karMiguel.capzip.dtos.usersDto.RegisterUserDto;
import io.github.karMiguel.capzip.model.users.Users;
import org.modelmapper.ModelMapper;

public class UserMapper {

    private static ModelMapper mapper = new ModelMapper();

    public static Users toUser(RegisterUserDto dto){
        return  mapper.map(dto, Users.class);
    }

}
