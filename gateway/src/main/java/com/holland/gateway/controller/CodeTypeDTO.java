package com.holland.gateway.controller;

import com.holland.common.entity.gateway.CodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CodeTypeDTO extends CodeType {
    private String dto="yes";
}
