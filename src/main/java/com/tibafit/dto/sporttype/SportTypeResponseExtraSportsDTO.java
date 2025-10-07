package com.tibafit.dto.sporttype;

import java.util.List;

import com.tibafit.dto.sport.SportResponseDTO;

public class SportTypeResponseExtraSportsDTO extends SportTypeResponseDTO {
    private List<SportResponseDTO> sportResponseDTOs;

    public List<SportResponseDTO> getSportResponseDTOs() {
        return sportResponseDTOs;
    }
    public void setSportResponseDTOs(List<SportResponseDTO> sportResponseDTOs) {
        this.sportResponseDTOs = sportResponseDTOs;
    }
}