package com.guoba.lisa.dtos;

import com.guoba.lisa.datamodel.Roll;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RollCallVo {
    private Roll roll;
    private Long stuId;
    private Integer credits;
}
