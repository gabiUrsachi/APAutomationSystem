//package org.example.presentation.controllers;
//
//import org.example.business.services.FilesService;
//import org.example.customexceptions.ObjectNotFoundException;
//import org.example.utils.ErrorMessages;
//import org.example.utils.ExceptionResponseDTO;
//import org.example.utils.data.Roles;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThrows;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//@RunWith(MockitoJUnitRunner.class)
//public class FilesControllerTest {
//    FilesController filesController;
//    FilesControllerAdvice filesControllerAdvice;
//
//    @Mock
//    ObjectNotFoundException objectNotFoundException;
//    @Mock
//    FilesService filesService;
//    @Mock
//    HttpServletRequest request;
//
//    @Before
//    public void setUp() {
//        filesController = new FilesController(filesService);
//        filesControllerAdvice = new FilesControllerAdvice();
//    }
//
//    @Test
//    public void throwObjectNotFoundExceptionWhenObjectDoesNotExist(){
//        UUID searchedUUID = UUID.randomUUID();
//        String keyName = "abcd";
//
//        given(request.getAttribute("roles")).willReturn(List.of(Roles.BUYER_CUSTOMER));
//        given(request.getAttribute("company")).willReturn(searchedUUID);
//        given(filesService.getFile(searchedUUID.toString(), keyName)).willAnswer((answer) -> { throw new ObjectNotFoundException(ErrorMessages.OBJECT_NOT_FOUND, keyName); });
//
//        assertThrows(ObjectNotFoundException.class, () -> filesController.getFile(keyName, request));
//
//        verify(request).getAttribute("roles");
//        verify(request).getAttribute("company");
//        verify(filesService).getFile(searchedUUID.toString(), keyName);
//    }
//
//    @Test
//    public void returnNotFoundStatusWhenOrderDoesNotExist() {
//        ResponseEntity<ExceptionResponseDTO> exceptionResponse = filesControllerAdvice.handleObjectNotFoundException2(objectNotFoundException);
//
//        assertEquals(HttpStatus.NOT_FOUND, exceptionResponse.getStatusCode());
//    }
//}