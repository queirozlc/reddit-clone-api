package com.lucas.redditclone.exception.bad_request;

import com.lucas.redditclone.exception.ExceptionDetails;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class BadRequestExceptionDetails extends ExceptionDetails {
}
