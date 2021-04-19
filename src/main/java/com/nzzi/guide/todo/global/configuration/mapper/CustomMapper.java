package com.nzzi.guide.todo.global.configuration.mapper;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

public class CustomMapper extends ModelMapper {

    private static class MapperHelper {
        private static final CustomMapper INSTANCE = new CustomMapper();
    }

    private CustomMapper() {
        super
            .getConfiguration()
            .setFieldMatchingEnabled(true)
            .setPropertyCondition(Conditions.isNotNull())
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
    }

    public static CustomMapper getInstance() {
        return MapperHelper.INSTANCE;
    }
}
