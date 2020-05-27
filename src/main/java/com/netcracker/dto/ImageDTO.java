package com.netcracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ImageDTO {
    @NonNull
    private String extension;
    @NonNull
    private byte[] value;
    @NonNull
    private String name;
}
