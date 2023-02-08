package com.lucas.redditclone.exception.not_found;

import com.lucas.redditclone.exception.ExceptionDetails;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class NotFoundExceptionDetails extends ExceptionDetails {
}
