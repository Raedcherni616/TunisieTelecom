package com.example.demo.technician;

import java.util.List;
import java.util.Optional;


public interface TechnicianService {

        Technician createTechnician(TechnicianRequest technicianRequest);

        Technician updateTechnician(TechnicianRequest technicianRequest);

        void deleteTechnician(Long id);

        Optional<Technician> findTechnicianById(Long id);

        List<Technician> getAllTechnicians();

        List<Technician> getTechniciansByRegion(Region region);

        List<Technician> getAvailableTechnicians();

        List<Technician> getNotAvailableTechnicians();

        List<Technician> getAvailableTechniciansByRegion(String city);


}
