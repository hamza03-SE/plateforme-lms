//package com.coopchal.lms.controllers;
//
//import com.coopchal.lms.models.Presence;
//import com.coopchal.lms.servicestest.PresenceService;
//import com.coopchal.lms.servicestest.UtilisateurService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/presences")
//@RequiredArgsConstructor
//public class PresenceController {
//
//    private final PresenceService presenceService;
//    private final UtilisateurService utilisateurService;
//
//    @PostMapping("/manuelle")
//    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
//    public Presence marquerManuel(@RequestParam Long idApprenant,
//                                  @RequestParam Long idCours,
//                                  @RequestParam boolean estPresent,
//                                  @RequestParam(required = false)
//                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        return presenceService.marquerPresenceManuelle(
//                idApprenant, idCours, date != null ? date : LocalDate.now(), estPresent);
//    }
//
//    @PostMapping("/auto/{idCours}")
//    @PreAuthorize("hasRole('APPRENANT')")
//    public Presence auto(Authentication auth, @PathVariable Long idCours) {
//        Long idApprenant = utilisateurService.trouverParEmail(auth.getName()).orElseThrow().getId();
//        return presenceService.autoDeclarationPresence(idApprenant, idCours);
//    }
//
//    @GetMapping("/cours/{idCours}")
//    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
//    public List<Presence> rapport(@PathVariable Long idCours) {
//        return presenceService.rapportParCours(idCours);
//    }
//
//    @GetMapping("/cours/{idCours}/apprenant/{idApprenant}")
//    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
//    public List<Presence> getParApprenant(@PathVariable Long idCours, @PathVariable Long idApprenant) {
//        return presenceService.presencesParApprenant(idApprenant, idCours);
//    }
//}
