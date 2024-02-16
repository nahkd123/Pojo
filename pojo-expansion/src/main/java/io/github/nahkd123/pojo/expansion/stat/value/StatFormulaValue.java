package io.github.nahkd123.pojo.expansion.stat.value;

import java.util.random.RandomGenerator;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.runtime.execution.Evaluator;
import xyz.mangostudio.mangoscript.runtime.execution.ExecutionContext;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.NumberValue;
import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.DynamicTokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;
import xyz.mangostudio.mangoscript.text.parser.Parsers;

public class StatFormulaValue implements StatValue {
	private String formula;

	// MangoScript
	private ModuleContext formulaModule;
	private Expression compiledFormula;

	public StatFormulaValue(String formula) {
		this.formula = formula;
		compile();
	}

	public String getFormula() { return formula; }

	private void compile() {
		ReaderStringStream string = new ReaderStringStream(formula);
		StringStreamLexer lexer = new StringStreamLexer(Token.FACTORY, string);
		DynamicTokenStream tokens = new DynamicTokenStream(lexer::nextToken);
		compiledFormula = Parsers.ALL.parseExpression(tokens);

		formulaModule = ModuleContext.create();
		// TODO add math functions
	}

	@Override
	public double get(RandomGenerator random) {
		ExecutionContext exec = formulaModule.newExecution();
		Value value = Evaluator.evaluate(exec, compiledFormula);
		return value instanceof NumberValue num ? num.getJvmNumber().doubleValue() : 0d;
	}

	@Override
	public String getDisplayText() { return "&e&o" + formula; } // TODO highlighting with tokenizer
}
