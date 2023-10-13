package org.example.presentation.controllers;

import org.example.business.services.FilesService;
import org.example.utils.AuthorizationMapper;
import org.example.utils.data.JwtClaims;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/files")
public class FilesController {

    private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping(path = "/{fileIdentifier}", produces = "application/pdf")
    public Resource getFile(@PathVariable String fileIdentifier, HttpServletRequest request) {
        JwtClaims jwtClaims = AuthorizationMapper.servletRequestToJWTClaims(request);
        String fileBucket = jwtClaims.getCompanyUUID().toString();

        Resource file = this.filesService.getFile(fileBucket, fileIdentifier);
        return file;
    }
}
