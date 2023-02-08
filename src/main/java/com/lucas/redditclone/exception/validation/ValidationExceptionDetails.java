package com.lucas.redditclone.exception.validation;

import com.lucas.redditclone.exception.ExceptionDetails;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ValidationExceptionDetails extends ExceptionDetails {
	private String fields;
}
