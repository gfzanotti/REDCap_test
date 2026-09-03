package mi.istitutotumori.tracing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class TraceAspect {

	private static final Logger logger = LogManager.getLogger(TraceAspect.class);
	private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

	/**
	 * Pointcut base: intercetta tutti i metodi nel package mi.istitutotumori
	 * Esclude automaticamente il package tracing e le classi AspectJ
	 */
	@Pointcut("execution(* mi.istitutotumori..*(..)) " + "&& !within(mi.istitutotumori.tracing..*) "
			+ "&& !within(org.aspectj..*)")
	public void baseApplicationCode() {
	}

	/**
	 * REGOLE DI ESCLUSIONE - Aggiungi qui i metodi/classi da escludere
	 * 
	 * Esempi: - !execution(* mi.istitutotumori.model..*(..)) -> esclude tutto il
	 * package model - !execution(* mi.istitutotumori.dto..*(..)) -> esclude tutto
	 * il package dto - !execution(* mi.istitutotumori.dao.UserDao.findAll(..)) ->
	 * esclude un metodo specifico - !execution(*
	 * mi.istitutotumori.utils.StringUtils.*(..)) -> esclude una classe intera
	 */
	@Pointcut("!execution(* mi.istitutotumori.model..*(..)) " + // Esclude model
			"&& !execution(* mi.istitutotumori.dto..*(..)) " + // Esclude dto
			"&& !execution(* mi.istitutotumori.config..*(..)) " + // Esclude config
			"&& !execution(* mi.istitutotumori.exception..*(..)) " + // Esclude exception
			"&& !execution(* mi.istitutotumori.e_crf.labutils..*(..)) " + // Esclude tutto package labutils
			"&& !execution(* mi.istitutotumori.e_crf.sanitize..*(..)) " + // Esclude tutto package sanitize
			"&& !execution(* mi.istitutotumori.e_crf.Field.FieldBuilder(..)) " + // Esclude metodo costruttore di
																					// FieldBuilder
			"&& !execution(* mi.istitutotumori.e_crf.Field.sanitize(..)) " + // Esclude metodo sanitize di Field
			"&& !execution(* mi.istitutotumori.e_crf.LabInstrument.*(..))" + // Esclude classe LabInstrument
			"&& !execution(* mi.istitutotumori.e_crf.ConfLabDataInstrument.*(..))" + // Esclude classe
																						// ConfLabDataInstrument
			"&& !execution(* mi.istitutotumori.e_crf.StandardInstrument.*(..))") // Esclude classe StandardInstrument
	public void exclusionRules() {
	}

	/**
	 * Pointcut combinato: applica le regole di esclusione alla base
	 */
	@Pointcut("baseApplicationCode() && exclusionRules()")
	public void applicationCode() {
	}

	@Around("applicationCode()")
	public Object trace(ProceedingJoinPoint pjp) throws Throwable {
		// Controlla se il metodo o la classe ha l'annotazione @ExcludeTrace
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		ExcludeTrace excludeAnnotation = signature.getMethod().getAnnotation(ExcludeTrace.class);
		if (excludeAnnotation == null) {
			excludeAnnotation = signature.getMethod().getDeclaringClass().getAnnotation(ExcludeTrace.class);
		}

		if (excludeAnnotation != null) {
			// Salta il tracing se annotato con @ExcludeTrace
			return pjp.proceed();
		}

		// Esegui il tracing normale
		int depth = DEPTH.get();
		String indent = "  ".repeat(depth);
		String methodName = pjp.getSignature().toShortString();

		logger.trace(String.format("%s--> %s", indent, methodName));

		long start = System.nanoTime();
		DEPTH.set(depth + 1);

		try {
			Object result = pjp.proceed();
			long elapsed = System.nanoTime() - start;
			double elapsedMs = elapsed / 1_000_000.0;
			logger.trace(String.format("%s<-- %s [%.3f ms]", indent, methodName, elapsedMs));
			return result;

		} catch (Throwable e) {
			long elapsed = System.nanoTime() - start;
			double elapsedMs = elapsed / 1_000_000.0;
			logger.error(String.format("%s<-- %s EXCEPTION [%.3f ms] - %s", indent, methodName, elapsedMs,
					e.getClass().getSimpleName()), e);
			throw e;

		} finally {
			DEPTH.set(depth);
		}
	}
}