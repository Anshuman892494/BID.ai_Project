const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('uuid');
const { getPool, isMemoryFallback, memoryDb } = require('../db');
const { JWT_SECRET } = require('../middleware/auth');

// Register a new user
exports.register = async (req, res) => {
  try {
    const { fullName, email, password, phone } = req.body;

    if (!fullName || !email || !password) {
      return res.status(400).json({
        status: 'error',
        message: 'fullName, email, and password are required fields.'
      });
    }

    const normalizedEmail = email.trim().toLowerCase();

    // Check if user already exists
    if (!isMemoryFallback()) {
      const pool = getPool();
      const existing = await pool.query('SELECT id FROM users WHERE email = $1', [normalizedEmail]);
      if (existing.rows.length > 0) {
        return res.status(409).json({
          status: 'error',
          message: 'An account with this email already exists.'
        });
      }

      const salt = await bcrypt.genSalt(10);
      const passwordHash = await bcrypt.hash(password, salt);

      const insertResult = await pool.query(
        `INSERT INTO users (full_name, email, phone, password_hash, bid_credits, eco_score)
         VALUES ($1, $2, $3, $4, 100, 50)
         RETURNING id, full_name, email, phone, bid_credits, eco_score, created_at`,
        [fullName.trim(), normalizedEmail, phone || '', passwordHash]
      );

      const user = insertResult.rows[0];
      const token = jwt.sign({ id: user.id, email: user.email }, JWT_SECRET, { expiresIn: '7d' });

      return res.status(201).json({
        status: 'success',
        message: 'User registered successfully',
        data: {
          token,
          user: {
            id: user.id,
            fullName: user.full_name,
            email: user.email,
            phone: user.phone,
            bidCredits: user.bid_credits,
            ecoScore: user.eco_score,
            createdAt: user.created_at
          }
        }
      });
    } else {
      // In-Memory implementation
      const existing = memoryDb.users.find(u => u.email === normalizedEmail);
      if (existing) {
        return res.status(409).json({
          status: 'error',
          message: 'An account with this email already exists.'
        });
      }

      const salt = await bcrypt.genSalt(10);
      const passwordHash = await bcrypt.hash(password, salt);

      const newUser = {
        id: uuidv4(),
        full_name: fullName.trim(),
        email: normalizedEmail,
        phone: phone || '',
        password_hash: passwordHash,
        bid_credits: 100,
        eco_score: 50,
        created_at: new Date()
      };

      memoryDb.users.push(newUser);
      const token = jwt.sign({ id: newUser.id, email: newUser.email }, JWT_SECRET, { expiresIn: '7d' });

      return res.status(201).json({
        status: 'success',
        message: 'User registered successfully',
        data: {
          token,
          user: {
            id: newUser.id,
            fullName: newUser.full_name,
            email: newUser.email,
            phone: newUser.phone,
            bidCredits: newUser.bid_credits,
            ecoScore: newUser.eco_score,
            createdAt: newUser.created_at
          }
        }
      });
    }
  } catch (err) {
    console.error('Registration error:', err);
    return res.status(500).json({
      status: 'error',
      message: 'Server error during registration.'
    });
  }
};

// Login user
exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        status: 'error',
        message: 'Email and password are required.'
      });
    }

    const normalizedEmail = email.trim().toLowerCase();

    let user = null;

    if (!isMemoryFallback()) {
      const pool = getPool();
      const result = await pool.query('SELECT * FROM users WHERE email = $1', [normalizedEmail]);
      if (result.rows.length === 0) {
        return res.status(401).json({
          status: 'error',
          message: 'Invalid email or password.'
        });
      }
      user = result.rows[0];
    } else {
      user = memoryDb.users.find(u => u.email === normalizedEmail);
      if (!user) {
        return res.status(401).json({
          status: 'error',
          message: 'Invalid email or password.'
        });
      }
    }

    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      return res.status(401).json({
        status: 'error',
        message: 'Invalid email or password.'
      });
    }

    const token = jwt.sign({ id: user.id, email: user.email }, JWT_SECRET, { expiresIn: '7d' });

    return res.status(200).json({
      status: 'success',
      message: 'Login successful',
      data: {
        token,
        user: {
          id: user.id,
          fullName: user.full_name,
          email: user.email,
          phone: user.phone,
          bidCredits: user.bid_credits,
          ecoScore: user.eco_score,
          createdAt: user.created_at
        }
      }
    });
  } catch (err) {
    console.error('Login error:', err);
    return res.status(500).json({
      status: 'error',
      message: 'Server error during login.'
    });
  }
};

// Get current user profile
exports.getProfile = async (req, res) => {
  try {
    const userId = req.user.id;
    let user = null;

    if (!isMemoryFallback()) {
      const pool = getPool();
      const result = await pool.query(
        'SELECT id, full_name, email, phone, bid_credits, eco_score, created_at FROM users WHERE id = $1',
        [userId]
      );
      if (result.rows.length === 0) {
        return res.status(404).json({
          status: 'error',
          message: 'User profile not found.'
        });
      }
      user = result.rows[0];
    } else {
      user = memoryDb.users.find(u => u.id === userId);
      if (!user) {
        return res.status(404).json({
          status: 'error',
          message: 'User profile not found.'
        });
      }
    }

    return res.status(200).json({
      status: 'success',
      data: {
        id: user.id,
        fullName: user.full_name,
        email: user.email,
        phone: user.phone,
        bidCredits: user.bid_credits,
        ecoScore: user.eco_score,
        createdAt: user.created_at
      }
    });
  } catch (err) {
    console.error('Get profile error:', err);
    return res.status(500).json({
      status: 'error',
      message: 'Server error fetching profile.'
    });
  }
};
