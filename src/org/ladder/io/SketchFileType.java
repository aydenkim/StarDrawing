/**
 * SketchFileType.java
 * 
 * Revision History:<br>
 * Jul 31, 2008 Aaron Wolin - File created
 * 
 * <p>
 * 
 * <pre>
 * This work is released under the BSD License:
 * (C) 2008 Sketch Recognition Lab, Texas A&amp;M University (hereafter SRL @ TAMU)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sketch Recognition Lab, Texas A&amp;M University 
 *       nor the names of its contributors may be used to endorse or promote 
 *       products derived from this software without specific prior written 
 *       permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY SRL @ TAMU ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SRL @ TAMU BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
package org.ladder.io;

/**
 * Enumeration for the types of sketches we support input and output for
 * 
 * @author awolin
 */
public enum SketchFileType {

	/**
	 * Uses the default {@link org.ladder.core.sketch.Point Point},
	 * {@link org.ladder.core.sketch.Stroke Stroke},
	 * {@link org.ladder.core.sketch.Segmentation Segmentation},
	 * {@link org.ladder.core.sketch.Shape Shape}, and
	 * {@link org.ladder.core.sketch.Sketch Sketch}
	 * 
	 */
	SRL,

	/**
	 * A DeepGreen sketch format
	 */
	DG,

	/**
	 * CivilSketch format: same as SRL, except that there is a difference
	 * between the global list of all strokes and the list of strokes contained
	 * directly by the sketch.
	 */
	CIVIL,

	/**
	 * SIMPL format: newer, stronger, better
	 */
	SIMPL
}
