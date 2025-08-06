//package com.coopchal.lms;
//
//import com.coopchal.lms.dtos.CoursDto;
//import com.coopchal.lms.enums.Role;
//import com.coopchal.lms.models.Cours;
//import com.coopchal.lms.models.Utilisateur;
//import com.coopchal.lms.repositories.CoursRepository;
//import com.coopchal.lms.repositories.UtilisateurRepository;
//import com.coopchal.lms.services.CoursService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class CoursServiceTest {
//
//    @Mock
//    private CoursRepository coursRepository;
//
//    @Mock
//    private UtilisateurRepository utilisateurRepository;
//
//    @InjectMocks
//    private CoursService coursService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testCreerCours_Success() {
//        // Arrange
//        Long formateurId = 1L;
//        Utilisateur formateur = new Utilisateur();
//        formateur.setId(formateurId);
//        formateur.setNom("Doe");
//        formateur.setPrenom("John");
//        formateur.setRole(Role.FORMATEUR);
//
//        CoursDto dto = new CoursDto(null, "Java", "Intro to Java", "java.png", null, formateurId);
//        Cours savedCours = new Cours();
//        savedCours.setId(1L);
//        savedCours.setTitre(dto.getTitre());
//        savedCours.setDescription(dto.getDescription());
//        savedCours.setImageUrl(dto.getImageUrl());
//        savedCours.setFormateur(formateur);
//
//        when(utilisateurRepository.findById(formateurId)).thenReturn(Optional.of(formateur));
//        when(coursRepository.save(any(Cours.class))).thenReturn(savedCours);
//
//        // Act
//        CoursDto result = coursService.creerCours(dto);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        assertEquals("Java", result.getTitre());
//        assertEquals("John Doe", result.getFormateurNomComplet());
//        verify(coursRepository, times(1)).save(any(Cours.class));
//    }
//
//    @Test
//    public void testCreerCours_Fail_FormateurNotFound() {
//        // Arrange
//        Long formateurId = 99L;
//        CoursDto dto = new CoursDto(null, "Python", "Basics", "python.png", null, formateurId);
//        when(utilisateurRepository.findById(formateurId)).thenReturn(Optional.empty());
//
//        // Act + Assert
//        RuntimeException ex = assertThrows(RuntimeException.class,
//                () -> coursService.creerCours(dto));
//        assertEquals("Formateur introuvable", ex.getMessage());
//    }
//
//    @Test
//    public void testCreerCours_Fail_NotFormateurRole() {
//        // Arrange
//        Long formateurId = 1L;
//        Utilisateur user = new Utilisateur();
//        user.setId(formateurId);
//        user.setRole(Role.APPRENANT); // mauvais rôle
//
//        CoursDto dto = new CoursDto(null, "Spring", "Framework", "spring.png", null, formateurId);
//        when(utilisateurRepository.findById(formateurId)).thenReturn(Optional.of(user));
//
//        // Act + Assert
//        RuntimeException ex = assertThrows(RuntimeException.class,
//                () -> coursService.creerCours(dto));
//        assertEquals("L'utilisateur n'est pas un formateur", ex.getMessage());
//    }
//
//    @Test
//    public void testGetCoursById_Success() {
//        // Arrange
//        Long coursId = 1L;
//        Utilisateur formateur = new Utilisateur();
//        formateur.setId(2L);
//        formateur.setNom("Doe");
//        formateur.setPrenom("Jane");
//
//        Cours cours = new Cours();
//        cours.setId(coursId);
//        cours.setTitre("DevOps");
//        cours.setDescription("CI/CD");
//        cours.setImageUrl("devops.png");
//        cours.setFormateur(formateur);
//
//        when(coursRepository.findById(coursId)).thenReturn(Optional.of(cours));
//
//        // Act
//        CoursDto result = coursService.getCoursById(coursId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("DevOps", result.getTitre());
//        assertEquals("Jane Doe", result.getFormateurNomComplet());
//    }
//
//    @Test
//    public void testUpdateCours_Success() {
//        // Arrange
//        Long coursId = 1L;
//        Cours existing = new Cours();
//        existing.setId(coursId);
//        existing.setTitre("Old");
//        existing.setDescription("Old desc");
//        existing.setImageUrl("old.png");
//
//        when(coursRepository.findById(coursId)).thenReturn(Optional.of(existing));
//        when(coursRepository.save(any(Cours.class))).thenAnswer(i -> i.getArgument(0));
//
//        CoursDto dto = new CoursDto(null, "New", "New desc", "new.png", null, null);
//
//        // Act
//        CoursDto updated = coursService.updateCours(coursId, dto);
//
//        // Assert
//        assertEquals("New", updated.getTitre());
//        assertEquals("New desc", updated.getDescription());
//    }
//
////    @Test
////    public void testDeleteCours_Success() {
////        // Arrange
////        Long coursId = 1L;
////        when(coursRepository.existsById(coursId)).thenReturn(true);
////
////        // Act
////        coursService.deleteCours(coursId);
////
////        // Assert
////        verify(coursRepository, times(1)).deleteById(coursId);
////    }
//
////    @Test
////    public void testDeleteCours_Fail_NotFound() {
////        // Arrange
////        Long coursId = 999L;
////        when(coursRepository.existsById(coursId)).thenReturn(false);
////
////        // Act + Assert
////        RuntimeException ex = assertThrows(RuntimeException.class,
////                () -> coursService.deleteCours(coursId));
////        assertEquals("Cours non trouvé", ex.getMessage());
////    }
//}
