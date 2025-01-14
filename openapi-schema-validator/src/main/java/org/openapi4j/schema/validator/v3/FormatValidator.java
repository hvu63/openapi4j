package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.*;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;
import static org.openapi4j.core.validation.ValidationSeverity.WARNING;

/**
 * format keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-13" />
 */
class FormatValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1007, "Value '%s' does not match format '%s'.");
  private static final ValidationResult UNKNOWN_WARN = new ValidationResult(WARNING, 1008, "Format '%s' is unknown, validation passes.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(FORMAT, true);

  private static final Pattern BASE64_PATTERN = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
  private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-(?:0[0-9]{1}|1[0-2]{1})-(0?[1-9]|[12][0-9]|3[01])$");
  private static final Pattern DATETIME_PATTERN = Pattern.compile("^\\d{4}-(?:0[0-9]{1}|1[0-2]{1})-(0?[1-9]|[12][0-9]|3[01])[tT ]\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?([zZ]|[+-]\\d{2}:\\d{2})$");
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\S+@\\S+$");
  private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$");
  private static final Pattern IPV4_PATTERN = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
  private static final Pattern IPV6_PATTERN = Pattern.compile("^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$");
  private static final Pattern URI_PATTERN = Pattern.compile("(^[a-zA-Z][a-zA-Z0-9+-.]*:[^\\s]*$)|(^//[^\\s]*$)");
  private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

  private final String format;

  FormatValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    format = (schemaNode.isTextual()) ? schemaNode.textValue() : null;
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    if (format == null || valueNode.isNull()) {
      return false;
    }

    boolean validated;

    switch (format) {
      case FORMAT_INT32:
        validated = valueNode.isInt();
        break;
      case FORMAT_INT64:
        validated = valueNode.isInt() || valueNode.isLong();
        break;
      case FORMAT_FLOAT:
        validated = valueNode.isInt() || valueNode.isFloatingPointNumber();
        break;
      case FORMAT_DOUBLE:
        validated = valueNode.isNumber();
        break;
      case FORMAT_DECIMAL:
        validated = valueNode.isNumber();
      case FORMAT_BYTE:
        validated = !valueNode.isTextual() || BASE64_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      case FORMAT_BINARY:
      case FORMAT_PASSWORD:
        validated = valueNode.isTextual();
        break;
      case FORMAT_DATE:
        validated = !valueNode.isTextual() || DATE_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      case FORMAT_DATE_TIME:
        validated = !valueNode.isTextual() || DATETIME_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      case FORMAT_EMAIL:
        validated = !valueNode.isTextual() || EMAIL_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      case FORMAT_HOSTNAME:
        validated = !valueNode.isTextual() || HOSTNAME_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      case FORMAT_IPV4:
        validated = !valueNode.isTextual() || IPV4_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      case FORMAT_IPV6:
        validated = !valueNode.isTextual() || IPV6_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      case FORMAT_UUID:
        validated = !valueNode.isTextual() || UUID_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      case FORMAT_URI:
      case FORMAT_URIREF:
      case FORMAT_URI_REFERENCE:
        validated = !valueNode.isTextual() || URI_PATTERN.matcher(valueNode.textValue()).matches();
        break;
      default:
        validation.add(CRUMB_INFO, UNKNOWN_WARN, format);
        validated = true;
        break;
    }

    if (!validated) {
      validation.add(CRUMB_INFO, ERR, valueNode.asText(), format);
    }

    return false;
  }
}
