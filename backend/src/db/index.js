const { Pool } = require('pg');
const { v4: uuidv4 } = require('uuid');
const bcrypt = require('bcryptjs');

let pool = null;
let useMemoryFallback = false;

// In-memory data store as resilient fallback
const memoryDb = {
  users: [],
  categories: [
    { id: 1, name: 'All', icon_name: 'grid' },
    { id: 2, name: 'Mobiles', icon_name: 'phone' },
    { id: 3, name: 'Laptops', icon_name: 'laptop' },
    { id: 4, name: 'Electronics', icon_name: 'headphones' },
    { id: 5, name: 'Cameras', icon_name: 'camera' },
    { id: 6, name: 'Furniture', icon_name: 'chair' }
  ],
  products: [],
  bids: []
};

// Seed initial memory items
const seedMemoryData = async () => {
  if (memoryDb.users.length === 0) {
    const salt = await bcrypt.genSalt(10);
    const demoPasswordHash = await bcrypt.hash('password123', salt);
    
    const demoUser = {
      id: 'a0000000-0000-0000-0000-000000000001',
      full_name: 'Anshuman Demo',
      email: 'demo@bid.ai',
      phone: '+919876543210',
      password_hash: demoPasswordHash,
      bid_credits: 150,
      eco_score: 380,
      created_at: new Date()
    };
    memoryDb.users.push(demoUser);

    const initialProducts = [
      {
        id: 'p0000000-0000-0000-0000-000000000001',
        seller_id: demoUser.id,
        category_id: 2, // Mobiles
        title: 'Apple iPhone 15 Pro (128 GB) - Natural Titanium',
        description: 'Battery health 96%. Minimal wear, pristine screen with screen protector installed from day 1. Includes original box and braided Type-C cable.',
        starting_price: 65000.00,
        current_bid: 74500.00,
        condition: 'Like New',
        image_url: 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&q=80',
        estimated_eco_score: 85,
        status: 'ACTIVE',
        ends_at: new Date(Date.now() + 4 * 3600 * 1000), // ends in 4 hours
        created_at: new Date(Date.now() - 20 * 3600 * 1000),
        bids_count: 14
      },
      {
        id: 'p0000000-0000-0000-0000-000000000002',
        seller_id: demoUser.id,
        category_id: 3, // Laptops
        title: 'MacBook Air M2 13.6" (16GB RAM / 512GB SSD) - Space Gray',
        description: 'Apple Silicon M2, lightning fast for development and creative work. 42 battery cycles only. Includes 35W dual charger and sleeve.',
        starting_price: 72000.00,
        current_bid: 81000.00,
        condition: 'Like New',
        image_url: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&q=80',
        estimated_eco_score: 110,
        status: 'ACTIVE',
        ends_at: new Date(Date.now() + 9 * 3600 * 1000), // ends in 9 hours
        created_at: new Date(Date.now() - 15 * 3600 * 1000),
        bids_count: 19
      },
      {
        id: 'p0000000-0000-0000-0000-000000000003',
        seller_id: demoUser.id,
        category_id: 4, // Electronics
        title: 'Sony WH-1000XM5 Wireless Noise Canceling Headphones',
        description: 'Industry leading noise cancellation, silver finish. Box and carrying case intact. Lightly used for travel.',
        starting_price: 16000.00,
        current_bid: 19800.00,
        condition: 'Good',
        image_url: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&q=80',
        estimated_eco_score: 45,
        status: 'ACTIVE',
        ends_at: new Date(Date.now() + 2 * 3600 * 1000), // ends in 2 hours
        created_at: new Date(Date.now() - 30 * 3600 * 1000),
        bids_count: 8
      },
      {
        id: 'p0000000-0000-0000-0000-000000000004',
        seller_id: demoUser.id,
        category_id: 5, // Cameras
        title: 'Canon EOS R50 Mirrorless Camera with 18-45mm Lens Kit',
        description: 'Compact 4K vlogging and photography powerhouse. Shutter count under 2500. Dual battery kit and 64GB Extreme Pro card included.',
        starting_price: 38000.00,
        current_bid: 43500.00,
        condition: 'Like New',
        image_url: 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80',
        estimated_eco_score: 75,
        status: 'ACTIVE',
        ends_at: new Date(Date.now() + 14 * 3600 * 1000),
        created_at: new Date(Date.now() - 10 * 3600 * 1000),
        bids_count: 11
      },
      {
        id: 'p0000000-0000-0000-0000-000000000005',
        seller_id: demoUser.id,
        category_id: 6, // Furniture
        title: 'Herman Miller Sayl Ergonomic Office Chair - Black',
        description: 'Original Herman Miller 3D intelligent back support, fully adjustable arms and seat depth. Perfect condition for home office.',
        starting_price: 24000.00,
        current_bid: 27500.00,
        condition: 'Good',
        image_url: 'https://images.unsplash.com/photo-1580481077195-c3a82145d875?w=600&q=80',
        estimated_eco_score: 95,
        status: 'ACTIVE',
        ends_at: new Date(Date.now() + 22 * 3600 * 1000),
        created_at: new Date(Date.now() - 8 * 3600 * 1000),
        bids_count: 6
      },
      {
        id: 'p0000000-0000-0000-0000-000000000006',
        seller_id: demoUser.id,
        category_id: 2, // Mobiles
        title: 'Google Pixel 8 (128GB) - Hazel Gray',
        description: 'Pure Android experience, incredible AI camera features like Best Take & Magic Eraser. Mint condition with case.',
        starting_price: 42000.00,
        current_bid: 46200.00,
        condition: 'Like New',
        image_url: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&q=80',
        estimated_eco_score: 65,
        status: 'ACTIVE',
        ends_at: new Date(Date.now() + 6 * 3600 * 1000),
        created_at: new Date(Date.now() - 12 * 3600 * 1000),
        bids_count: 9
      }
    ];

    memoryDb.products.push(...initialProducts);
  }
};

const initDatabase = async () => {
  const connectionString = process.env.DATABASE_URL;
  
  if (connectionString) {
    try {
      pool = new Pool({
        connectionString,
        ssl: connectionString.includes('localhost') ? false : { rejectUnauthorized: false }
      });
      await pool.query('SELECT NOW()');
      console.log('✅ Connected to PostgreSQL Database');
      useMemoryFallback = false;
      return;
    } catch (err) {
      console.warn('⚠️ PostgreSQL connection failed, switching to in-memory fallback:', err.message);
      useMemoryFallback = true;
    }
  } else {
    console.log('ℹ️ No DATABASE_URL specified. Running with high-performance In-Memory PostgreSQL Store.');
    useMemoryFallback = true;
  }

  await seedMemoryData();
};

module.exports = {
  initDatabase,
  getPool: () => pool,
  isMemoryFallback: () => useMemoryFallback,
  memoryDb
};
