package com.project_manager_tool.backend.dto;

import com.project_manager_tool.backend.models.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toDto_shouldMapUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("john");
        user.setEmail("john@example.com");

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("john", dto.getUsername());
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    void toDto_shouldReturnNullIfUserIsNull() {
        assertNull(userMapper.toDto(null));
    }

    @Test
    void toEntity_shouldMapUserDto() {
        UserDto dto = new UserDto();
        dto.setId(2);
        dto.setUsername("jane");
        dto.setEmail("jane@example.com");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals("jane", user.getUsername());
        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    void toEntity_shouldReturnNullIfDtoIsNull() {
        assertNull(userMapper.toEntity(null));
    }

    @Test
    void toDtoList_shouldMapList() {
        User user1 = new User();
        user1.setId(1);
        User user2 = new User();
        user2.setId(2);

        List<UserDto> dtos = userMapper.toDtoList(List.of(user1, user2));

        assertEquals(2, dtos.size());
        assertEquals(1, dtos.get(0).getId());
        assertEquals(2, dtos.get(1).getId());
    }

    @Test
    void toDtoList_shouldReturnEmptyListIfNull() {
        List<UserDto> dtos = userMapper.toDtoList(null);
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }
}
