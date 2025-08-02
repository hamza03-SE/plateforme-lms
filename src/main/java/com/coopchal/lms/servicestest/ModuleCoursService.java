package com.coopchal.lms.servicestest;

import com.coopchal.lms.models.Cours;
import com.coopchal.lms.models.ModuleCours;
import com.coopchal.lms.repositories.CoursRepository;
import com.coopchal.lms.repositories.ModuleCoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleCoursService {

    private final ModuleCoursRepository moduleCoursRepository;
    private final CoursRepository coursRepository;
    private final MinioService minioService;

    /**
     * Ajoute un module à un cours sans fichier.
     */
    public ModuleCours ajouterModule(Long idCours, ModuleCours module) {
        Cours cours = coursRepository.findById(idCours)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        module.setCours(cours);
        return moduleCoursRepository.save(module);
    }

    /**
     * Ajoute un module à un cours avec un fichier stocké dans le bucket "cours".
     */
    public ModuleCours ajouterAvecFichier(Long idCours, ModuleCours module, MultipartFile fichier, String bucket) {
        // On passe le bucket choisi au service Minio
        String url = minioService.uploadFichierDansBucket(bucket, fichier, "modules");
        module.setUrlFichier(url);
        return ajouterModule(idCours, module);
    }
    /**
     * Liste tous les modules d’un cours spécifique.
     */
    public List<ModuleCours> modulesParCours(Long idCours) {
        return moduleCoursRepository.findByCoursId(idCours);
    }

    /**
     * Supprime un module (et le fichier associé dans MinIO s’il existe).
     */
    public void supprimerModule(Long idModule) {
        ModuleCours module = moduleCoursRepository.findById(idModule)
                .orElseThrow(() -> new RuntimeException("Module non trouvé"));

        if (module.getUrlFichier() != null && !module.getUrlFichier().isBlank()) {
            minioService.supprimerFichier(module.getUrlFichier());
        }

        moduleCoursRepository.deleteById(idModule);
    }

    /**
     * Vérifie si un fichier appartient à un module d’un cours spécifique.
     */
    public boolean fichierAppartientAuCours(Long idCours, String urlFichier) {
        return moduleCoursRepository.existsByCours_IdAndUrlFichier(idCours, urlFichier);
    }
}
