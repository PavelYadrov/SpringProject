package com.netcracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class DTOHelper {
    @NonNull
    private String firstLine;
    @NonNull
    private String secondLine;
}
